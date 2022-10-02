package com.piter.bet.api.service;

import com.piter.bet.api.domain.Bet;
import com.piter.bet.api.exception.BetNotFoundException;
import com.piter.bet.api.producer.BetKafkaProducer;
import com.piter.bet.api.repository.BetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BetServiceImpl implements BetService {

  private final BetRepository betRepository;
  private final BetKafkaProducer betKafkaProducer;

  @Override
  public Flux<Bet> findAll() {
    return betRepository.findAll();
  }

  @Override
  public Mono<Bet> findById(Long id) {
    return betRepository.findById(id);
  }

  @Override
  public Mono<Bet> saveBet(Bet bet) {
    return Mono.just(betKafkaProducer.sendSaveBetEvent(bet));
  }

  @Override
  public Mono<Void> deleteBet(Long id) {
    return betRepository.findById(id)
        .switchIfEmpty(Mono.error(new BetNotFoundException(id)))
        .flatMap(betRepository::delete);
  }
}
