package com.piter.match.api.repository;

import com.piter.api.commons.model.Match;
import java.time.LocalDateTime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MatchRepository extends ReactiveMongoRepository<Match, Long> {

  Flux<Match> findAllBy(Pageable pageable);

  Flux<Match> findByStartTimeAfter(LocalDateTime matchTime, Pageable pageable);

  Flux<Match> findByStartTimeBeforeAndResultIsNull(LocalDateTime matchTime, Pageable pageable);
}