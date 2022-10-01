package com.piter.bet.api.service;

import static com.piter.bet.api.util.BetTestData.createBetList;
import static org.assertj.core.api.Assertions.assertThat;

import com.piter.bet.api.config.BetApiTestConfig;
import com.piter.bet.api.domain.Bet;
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
    List<Bet> bets = createBetList();
    bets.forEach(bet -> betRepository.save(bet).block());
  }

  @Test
  void shouldGetBets() {
    Flux<Bet> betFlux = betService.findAll();
    StepVerifier.create(betFlux)
        .expectNextCount(2)
        .verifyComplete();
  }

  @Test
  void shouldGetBetById() {
    var id = 2L;
    Mono<Bet> betMono = betService.findById(id);
    StepVerifier.create(betMono)
        .assertNext(bet -> assertThat(bet.getId()).isEqualTo(id))
        .verifyComplete();
  }
}