package com.piter.bet.api.repository;

import com.piter.bet.api.domain.Bet;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface BetRepository extends ReactiveMongoRepository<Bet, Long> {

}
