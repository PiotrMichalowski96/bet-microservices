package com.piter.bet.api.service;

import static com.piter.bet.api.util.BetTestData.createBetList;
import static org.assertj.core.api.Assertions.assertThat;

import com.piter.api.commons.domain.Bet;
import com.piter.api.commons.domain.User;
import com.piter.bet.api.config.BetApiTestConfig;
import com.piter.bet.api.exception.BetNotFoundException;
import com.piter.bet.api.repository.BetRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@DataMongoTest
@ActiveProfiles("TEST")
@ExtendWith(SpringExtension.class)
@Import(BetApiTestConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class BetServiceTest {

  private static final List<Bet> BETS = createBetList();

  @Autowired
  private BetRepository betRepository;

  @Autowired
  private BetService betService;

  @BeforeEach
  void fillDatabaseIfEmpty() {
    List<Bet> existingBets = betRepository.findAll()
        .collectList()
        .block();
    if (existingBets == null || existingBets.isEmpty()) {
      fillDatabase();
    }
  }

  private void fillDatabase() {
    BETS.forEach(bet -> betRepository.save(bet).block());
  }

  @Test
  void shouldGetAllVisibleBets() {
    var user = BETS.get(0).getUser();
    Flux<Bet> betFlux = betService.findAll(user);
    StepVerifier.create(betFlux)
        .assertNext(bet -> assertThat(bet.getId()).isEqualTo(1L))
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
    var user = BETS.get(1).getUser();
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