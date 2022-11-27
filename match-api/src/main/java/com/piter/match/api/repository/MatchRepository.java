package com.piter.match.api.repository;

import com.piter.api.commons.domain.Match;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MatchRepository extends ReactiveMongoRepository<Match, Long> {

  Flux<Match> findAllByOrderByStartTimeDesc();

  Flux<Match> findAllByOrderByStartTimeAsc();

  Flux<Match> findAllByOrderByRoundStartTimeDesc();
}