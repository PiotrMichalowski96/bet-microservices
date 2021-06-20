package com.piter.bets.league.eurobets.service;

import com.piter.bets.league.eurobets.config.PageDetails;
import com.piter.bets.league.eurobets.dto.MatchDTO;
import com.piter.bets.league.eurobets.entity.Bet;
import com.piter.bets.league.eurobets.entity.Match;
import com.piter.bets.league.eurobets.entity.MatchResult;
import com.piter.bets.league.eurobets.entity.MatchRound;
import com.piter.bets.league.eurobets.entity.User;
import com.piter.bets.league.eurobets.exception.MatchNotFoundException;
import com.piter.bets.league.eurobets.exception.MatchRoundNotFoundException;
import com.piter.bets.league.eurobets.mapper.MatchMapper;
import com.piter.bets.league.eurobets.repository.BetRepository;
import com.piter.bets.league.eurobets.repository.BetResultsRepository;
import com.piter.bets.league.eurobets.repository.MatchRepository;
import com.piter.bets.league.eurobets.repository.MatchRoundRepository;
import com.piter.bets.league.eurobets.repository.UserRepository;
import com.piter.bets.league.eurobets.repository.projection.UserPoints;
import com.piter.bets.league.eurobets.util.BetRules;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

  private final MatchRepository matchRepository;
  private final MatchRoundRepository matchRoundRepository;
  private final BetRepository betRepository;
  private final BetResultsRepository betResultsRepository;
  private final UserRepository userRepository;
  private final MatchMapper matchMapper;

  @Override
  public List<MatchDTO> findAllByMatchStartTime(Integer pageNumber) {
    Pageable pageable = PageRequest.of(pageNumber, PageDetails.SIZE);
    List<Match> matches = matchRepository.findAllByOrderByMatchStartTimeDesc(pageable);

    return matches.stream()
        .map(matchMapper::toMatchDTO)
        .collect(Collectors.toList());
  }

  @Override
  public List<MatchDTO> findAllByMatchRound(Integer pageNumber) {
    Pageable pageable = PageRequest.of(pageNumber, PageDetails.SIZE);
    List<Match> matches = matchRepository.findTAllByOrderByMatchRoundStartTimeDesc(pageable);

    return matches.stream()
        .map(matchMapper::toMatchDTO)
        .collect(Collectors.toList());
  }

  @Override
  public List<MatchDTO> findAll(Integer pageNumber) {
    Sort sort = Sort.by(Sort.Direction.ASC, "id");
    Pageable pageable = PageRequest.of(pageNumber, PageDetails.SIZE, sort);
    Page<Match> matches = matchRepository.findAll(pageable);

    return matches.stream()
        .map(matchMapper::toMatchDTO)
        .collect(Collectors.toList());
  }

  @Override
  public MatchDTO findById(Long id) throws MatchNotFoundException {
    Match match = matchRepository.findById(id)
        .orElseThrow(() -> new MatchNotFoundException("Cannot find match with id: " + id));

    return matchMapper.toMatchDTO(match);
  }

  @Override
  public MatchDTO save(MatchDTO matchDTO) throws MatchRoundNotFoundException {
    Match match = matchMapper.toMatch(matchDTO);
    Long matchRoundId = matchDTO.getMatchRoundId();
    MatchRound matchRound = matchRoundRepository.findById(matchRoundId)
        .orElseThrow(() -> new MatchRoundNotFoundException(
            "Cannot find match round with id: " + matchRoundId));

    if(checkIfContainMatchResult(match)) {
       addBetResults(match);
       calculatePointsForAllUsers();
    }

    match.setMatchRound(matchRound);
    return matchMapper.toMatchDTO(matchRepository.save(match));
  }

  @Override
  public void delete(Long id) throws MatchNotFoundException {
    Match match = matchRepository.findById(id)
        .orElseThrow(() -> new MatchNotFoundException("Cannot find match with id: " + id));

    matchRepository.delete(match);
  }

  private boolean checkIfContainMatchResult(Match match) {
    MatchResult matchResult = match.getMatchResult();
    if(Objects.isNull(matchResult)) {
      return false;
    } else {
      return matchResult.getHomeTeamGoals() != null && matchResult.getAwayTeamGoals() != null;
    }
  }

  private void addBetResults(Match match) {
    List<Bet> bets = betRepository.findByMatchId(match.getId());

    MatchResult matchResult = match.getMatchResult();

    List<Bet> betsWithResults = bets.stream()
        .map(bet -> BetRules.getBetWithResult(bet, matchResult))
        .collect(Collectors.toList());

    betRepository.saveAll(betsWithResults);
  }

  private void calculatePointsForAllUsers() {

    List<UserPoints> userPointsList = betResultsRepository.sumTotalPointsForUsers();

    List<User> updatedUsers = userPointsList.stream()
        .map(userPoints -> {
          User user = userPoints.getUser();
          Long points = userPoints.getPoints();
          user.setPoints(points);
          return user;
        })
        .collect(Collectors.toList());

    userRepository.saveAll(updatedUsers);
  }
}
