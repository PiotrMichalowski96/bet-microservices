package com.piter.bet.api.service;

import com.piter.api.commons.domain.Bet;
import com.piter.api.commons.domain.Match;
import com.piter.api.commons.domain.User;
import com.piter.bet.api.exception.BetNotFoundException;
import com.piter.bet.api.producer.BetKafkaProducer;
import com.piter.bet.api.repository.BetRepository;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class BetService {

  private final BetRepository betRepository;
  private final BetKafkaProducer betKafkaProducer;

  public Flux<Bet> findAll(User user) {
    return betRepository.findAll()
        .filter(bet -> isBetVisibleForUser(user, bet));
  }

  public Mono<Bet> findById(Long id, User user) {
    return betRepository.findById(id)
        .filter(bet -> isBetVisibleForUser(user, bet))
        .switchIfEmpty(Mono.error(new BetNotFoundException(id)));
  }

  public Mono<Bet> saveBet(Bet bet) {
    return Mono.just(betKafkaProducer.sendSaveBetEvent(bet));
  }

  public Mono<Void> deleteBet(Long id, User user) {
    return betRepository.findById(id)
        .filter(bet -> doesUserOwnBet(user, bet))
        .switchIfEmpty(Mono.error(new BetNotFoundException(id)))
        .flatMap(betRepository::delete);
  }

  private boolean isBetVisibleForUser(User user, Bet bet) {
    return doesUserOwnBet(user, bet) || isMatchStarted(bet.getMatch());
  }

  private boolean isMatchStarted(Match match) {
    return LocalDateTime.now().isAfter(match.getStartTime());
  }

  private boolean doesUserOwnBet(User user, Bet bet) {
    return Objects.equals(user, bet.getUser());
  }
}
