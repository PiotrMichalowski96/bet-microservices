package com.piter.bet.api.service;

import static com.piter.bet.api.util.BetTestData.createBetList;
import static org.assertj.core.api.Assertions.assertThat;

import com.piter.api.commons.domain.Bet;
import com.piter.api.commons.domain.User;
import com.piter.bet.api.exception.BetNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class BetServiceTest extends AbstractServiceTest {

  private final BetService betService;

  @Autowired
  public BetServiceTest(BetService betService) {
    super(createBetList());
    this.betService = betService;
  }

  @Test
  void shouldGetAllVisibleBets() {
    var user = super.getBets().get(0).getUser();
    Flux<Bet> betFlux = betService.findAll(user);
    StepVerifier.create(betFlux)
        .assertNext(bet -> assertThat(bet.getId()).isEqualTo(1L))
        .assertNext(bet -> assertThat(bet.getId()).isEqualTo(3L))
        .verifyComplete();
  }

  @Test
  void shouldGetAllVisibleBetsByMatchId() {
    var matchId = 1L;
    var user = super.getBets().get(0).getUser();
    Flux<Bet> betFlux = betService.findAllByMatchId(matchId, user);
    StepVerifier.create(betFlux)
        .assertNext(bet -> assertThat(bet.getId()).isEqualTo(1L))
        .verifyComplete();
  }

  @Test
  void shouldGetAllVisibleBetsByUserNickname() {
    var nickname = super.getBets().get(2).getUser().getNickname();
    var user = super.getBets().get(0).getUser();
    Flux<Bet> betFlux = betService.findAllByUserNickname(nickname, user);
    StepVerifier.create(betFlux)
        .assertNext(bet -> assertThat(bet.getId()).isEqualTo(3L))
        .verifyComplete();
  }

  @Test
  void shouldGetBetsWhenMatchIsStarted() {
    var user = new User("Arya", "Stark", "needle");
    Flux<Bet> betFlux = betService.findAll(user);
    StepVerifier.create(betFlux)
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  void shouldGetBetById() {
    var id = 2L;
    var user = super.getBets().get(1).getUser();
    Mono<Bet> betMono = betService.findById(id, user);
    StepVerifier.create(betMono)
        .assertNext(bet -> assertThat(bet.getId()).isEqualTo(id))
        .verifyComplete();
  }

  @Test
  void shouldNotGetBetByIdBecauseUserIsNotOwner() {
    var id = 2L;
    var user = new User("Arya", "Stark", "needle");
    Mono<Bet> betMono = betService.findById(id, user);
    StepVerifier.create(betMono)
        .expectError(BetNotFoundException.class)
        .verify();
  }
}