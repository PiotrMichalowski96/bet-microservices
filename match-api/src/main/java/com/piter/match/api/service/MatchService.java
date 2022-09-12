package com.piter.match.api.service;

import com.piter.match.api.domain.Match;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MatchService {

  Flux<Match> findAll();

  Flux<Match> findAllByOrderByMatchStartTime();

  Flux<Match> findAllByOrderByMatchRoundStartTime();

  Mono<Match> findById(Long id);

  Mono<Match> saveMatch(Match match);

  Mono<Void> deleteMatch(Long id);
}
