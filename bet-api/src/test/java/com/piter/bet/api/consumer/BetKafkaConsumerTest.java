package com.piter.bet.api.consumer;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import com.piter.api.commons.domain.Bet;
import com.piter.api.commons.domain.MatchResult;
import com.piter.bet.api.config.BetApiTestConfig;
import com.piter.bet.api.repository.BetRepository;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest
@ActiveProfiles("TEST")
@ExtendWith(SpringExtension.class)
@Import(BetApiTestConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class BetKafkaConsumerTest {

  private static final int TIMEOUT_IN_SECONDS = 20;
  private static final int POLL_INTERVAL_IN_SECONDS = 2;

  @Autowired
  private BetKafkaConsumer betKafkaConsumer;

  @Autowired
  private BetRepository betRepository;

  @Test
  void shouldSaveBet() {
    //given
    var id = 1L;
    var bet = Bet.builder()
        .id(id)
        .matchPredictedResult(new MatchResult(1, 1))
        .build();
    var betMessage = MessageBuilder
        .withPayload(bet)
        .build();

    //when
    betKafkaConsumer.bets().accept(betMessage);

    //then
    assertAsync(() -> assertSavedBet(id, bet));
  }

  private void assertSavedBet(long id, Bet expectedBet) {
    var savedBet = betRepository.findById(id).block();
    assertThat(savedBet).isEqualTo(expectedBet);
  }

  private void assertAsync(ThrowingRunnable assertion) {
    Awaitility.await()
        .atMost(TIMEOUT_IN_SECONDS, SECONDS)
        .pollInterval(POLL_INTERVAL_IN_SECONDS, SECONDS)
        .untilAsserted(assertion);
  }
}