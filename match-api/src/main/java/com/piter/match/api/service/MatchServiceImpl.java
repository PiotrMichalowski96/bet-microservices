package com.piter.match.api.service;

import com.piter.match.api.domain.Match;
import com.piter.match.api.repository.MatchRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

  private final MatchRepository matchRepository;

  @Override
  public Mono<List<Match>> findAll() {
    return matchRepository.findAll()
        .collect(Collectors.toList());
  }

  @Override
  public Mono<List<Match>> findAllByOrderByMatchStartTime() {
    return matchRepository.findAllByOrderByStartTimeDesc()
        .collect(Collectors.toList());
  }

  @Override
  public Mono<List<Match>> findAllByOrderByMatchRoundStartTime() {
    return matchRepository.findAllByOrderByRoundStartTimeDesc()
        .collect(Collectors.toList());
  }

  @Override
  @Cacheable("matches")
  public Mono<Match> findById(Long id) {
    return matchRepository.findById(id).cache();
  }
}
