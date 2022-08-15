package com.piter.match.api.service;

import com.piter.match.api.domain.Match;
import java.util.List;
import reactor.core.publisher.Mono;

public interface MatchService {

  Mono<List<Match>> findAll();

  Mono<List<Match>> findAllByOrderByMatchStartTime();

  Mono<List<Match>> findAllByOrderByMatchRoundStartTime();

  Mono<Match> findById(Long id);
}
