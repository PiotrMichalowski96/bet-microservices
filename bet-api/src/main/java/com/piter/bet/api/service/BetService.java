package com.piter.bet.api.service;

import com.piter.bet.api.domain.Bet;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface BetService {

  Flux<Bet> findAll();

  Mono<Bet> findById(Long id);

  Mono<Bet> saveBet(Bet bet);

  Mono<Void> deleteBet(Long id);
}
