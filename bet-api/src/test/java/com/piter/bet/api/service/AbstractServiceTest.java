package com.piter.bet.api.service;

import com.piter.api.commons.domain.Bet;
import com.piter.bet.api.config.BetApiTestConfig;
import com.piter.bet.api.repository.BetRepository;
import java.util.List;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

@DataMongoTest
@ActiveProfiles("TEST")
@ExtendWith(SpringExtension.class)
@Import(BetApiTestConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public abstract class AbstractServiceTest {

  @Autowired
  private BetRepository betRepository;

  @Getter
  private final List<Bet> bets;

  public AbstractServiceTest(List<Bet> bets) {
    this.bets = bets;
  }

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
    bets.forEach(bet -> betRepository.save(bet).block());
  }

  protected Mono<Bet> findBetBy(String id) {
    return betRepository.findById(id);
  }

  protected void saveBet(Bet bet) {
    betRepository.save(bet).block();
  }

  protected void deleteBet(Bet bet) {
    betRepository.deleteById(bet.getId()).block();
  }
}
