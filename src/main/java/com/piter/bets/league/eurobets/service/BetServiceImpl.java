package com.piter.bets.league.eurobets.service;

import com.piter.bets.league.eurobets.config.PageDetails;
import com.piter.bets.league.eurobets.dto.BetDTO;
import com.piter.bets.league.eurobets.entity.Bet;
import com.piter.bets.league.eurobets.entity.Match;
import com.piter.bets.league.eurobets.entity.MatchRound;
import com.piter.bets.league.eurobets.entity.User;
import com.piter.bets.league.eurobets.exception.BetNotFoundException;
import com.piter.bets.league.eurobets.exception.MatchNotFoundException;
import com.piter.bets.league.eurobets.exception.BettingRulesException;
import com.piter.bets.league.eurobets.exception.UnauthorizedUserException;
import com.piter.bets.league.eurobets.exception.UserNotFoundException;
import com.piter.bets.league.eurobets.mapper.BetMapper;
import com.piter.bets.league.eurobets.repository.BetRepository;
import com.piter.bets.league.eurobets.repository.MatchRepository;
import com.piter.bets.league.eurobets.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class BetServiceImpl implements BetService {

  private final BetRepository betRepository;
  private final MatchRepository matchRepository;
  private final UserRepository userRepository;
  private final BetMapper betMapper;

  @Override
  public List<BetDTO> findAllByUserId(Integer pageNumber, Long userId, Long myUserId) {
    Pageable pageable = PageRequest.of(pageNumber, PageDetails.SIZE);
    return betRepository.findByUserId(userId, pageable).stream()
        .filter(getIsVisiblePredicate(myUserId))
        .map(betMapper::toBetDTO)
        .collect(Collectors.toList());
  }

  @Override
  public BetDTO findById(Long betId, Long myUserId) throws BetNotFoundException {
    return betRepository.findById(betId)
        .filter(getIsVisiblePredicate(myUserId))
        .map(betMapper::toBetDTO)
        .orElseThrow(() -> new BetNotFoundException("Bet doesnt exist or it is not visible for you. Bet id: " + betId));
  }

  @Override
  public List<BetDTO> findAllThatShouldBeVisible(Integer pageNumber, Long myUserId) {
    return betRepository.findAll().stream()
        .filter(getIsVisiblePredicate(myUserId))
        .map(betMapper::toBetDTO)
        .collect(Collectors.toList());
  }

  @Override
  public BetDTO save(Long userId, Long matchId, BetDTO betDTO)
      throws MatchNotFoundException, BettingRulesException, UserNotFoundException {

    Match match = matchRepository.findById(matchId)
        .orElseThrow(() -> new MatchNotFoundException("Cannot find match with id: " + matchId));

    if (checkIfMatchRoundTimeStartNow(match)) {
      throw new BettingRulesException("You can only bet until match round is not started");
    }

    List<Bet> bets = betRepository.findByUserIdAndMatchId(userId, matchId);

    if (checkIfThereIsMoreThanOneBetForMatch(bets)) {
      String message = String.format("User id: %d has more than one bet for the match id: %d", userId, matchId);
      throw new BettingRulesException(message);
    }

    Bet bet = betMapper.toBet(betDTO);

    if (!bets.isEmpty()) {
      Long betId = bets.get(0).getId();
      bet.setId(betId);
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new UserNotFoundException("Cannot find user with id: " + userId));

    bet.setMatch(match);
    bet.setUser(user);

    return betMapper.toBetDTO(betRepository.save(bet));
  }

  @Override
  public void delete(Long betId, Long userId) throws UnauthorizedUserException {
    Optional<Bet> betOptional = betRepository.findById(betId)
        .filter(bet -> checkIfUserOwnsBet(userId, bet))
        .filter(bet -> Objects.isNull(bet.getBetResults())); //checking if bet doesnt contain result

    if(betOptional.isPresent()) {
      betRepository.delete(betOptional.get());
    } else {
      throw new UnauthorizedUserException("You are not the owner of the bet or bet contains result");
    }
  }

  /**
   * All our bets (with our userId) should be visible Bets of other users should be visible after
   * start match round time
   */
  private Predicate<Bet> getIsVisiblePredicate(Long userId) {
    return bet -> {
      LocalDateTime now = LocalDateTime.now();
      MatchRound matchRound = bet.getMatch().getMatchRound();
      LocalDateTime startMatchRoundTime = matchRound.getStartRoundTime();

      Long betUserId = bet.getUser().getId();

      return now.isAfter(startMatchRoundTime) || Objects.equals(betUserId, userId);
    };
  }

  /**
   * Bet is only available before match round start time
   */
  private boolean checkIfMatchRoundTimeStartNow(Match match) {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime startMatchRoundTime = match.getMatchRound().getStartRoundTime();
    return now.isAfter(startMatchRoundTime);
  }

  /**
   * There is only available to once bet one match
   */
  private boolean checkIfThereIsMoreThanOneBetForMatch(List<Bet> bets) {
    return bets.size() > 1;
  }

  private boolean checkIfUserOwnsBet(Long userId, Bet bet) {
    Long ownerBetId = bet.getUser().getId();
    return Objects.equals(userId, ownerBetId);
  }
}
