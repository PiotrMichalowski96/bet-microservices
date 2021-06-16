package com.piter.bets.league.eurobets.service;

import com.piter.bets.league.eurobets.config.PageDetails;
import com.piter.bets.league.eurobets.dto.MatchDTO;
import com.piter.bets.league.eurobets.entity.Match;
import com.piter.bets.league.eurobets.exception.MatchNotFoundException;
import com.piter.bets.league.eurobets.mapper.MatchMapper;
import com.piter.bets.league.eurobets.repository.MatchRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

  private final MatchRepository matchRepository;
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
  public List<MatchDTO> findAll(Integer pageNumber) {
    Sort sort = Sort.by(Sort.Direction.ASC, "id");
    List<Match> matches = matchRepository.findAll(sort);

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
  public MatchDTO save(MatchDTO matchDTO) {
    Match match = matchMapper.toMatch(matchDTO);
    return matchMapper.toMatchDTO(matchRepository.save(match));
  }

  @Override
  public void delete(Long id) throws MatchNotFoundException {
    Match match = matchRepository.findById(id)
        .orElseThrow(() -> new MatchNotFoundException("Cannot find match with id: " + id));

    matchRepository.delete(match);
  }
}
