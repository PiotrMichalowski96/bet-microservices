package com.piter.bet.api.repository;

import com.piter.api.commons.domain.Bet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface BetRepository extends ReactiveMongoRepository<Bet, Long> {

  Flux<Bet> findAllByMatchId(Long matchId);

  Flux<Bet> findAllByUserNickname(String nickname);
}
