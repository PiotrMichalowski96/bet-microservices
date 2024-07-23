package com.piter.match.api.consumer;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import com.piter.api.commons.model.Match;
import com.piter.api.commons.model.MatchResult;
import com.piter.api.commons.model.MatchRound;
import com.piter.match.api.config.MatchApiTestConfig;
import com.piter.match.api.repository.MatchRepository;
import java.time.LocalDateTime;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Tag("EmbeddedMongoDBTest")
@DataMongoTest
@ActiveProfiles("TEST")
@ExtendWith(SpringExtension.class)
@Import(MatchApiTestConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class MatchKafkaConsumerIntegrationTest {

  private static final int TIMEOUT_IN_SECONDS = 20;
  private static final int POLL_INTERVAL_IN_SECONDS = 2;

  @Autowired
  private MatchKafkaConsumer matchKafkaConsumer;

  @Autowired
  private MatchRepository matchRepository;

  @Test
  void shouldUpsertMatch() {
    //given
    var id = 99L;
    var match = Match.builder()
        .id(id)
        .homeTeam("FC Barcelona")
        .awayTeam("Real Madrid")
        .startTime(LocalDateTime.of(2022, 2, 17, 21, 0, 0))
        .result(new MatchResult(4, 0))
        .round(new MatchRound("LaLiga round 30", LocalDateTime.of(2022, 2, 14, 21, 0, 0)))
        .build();

    matchRepository.save(match).block();

    var updatedMatch = Match.builder()
        .id(match.id())
        .homeTeam(match.homeTeam())
        .awayTeam(match.awayTeam())
        .startTime(match.startTime())
        .result(new MatchResult(4, 2))
        .round(match.round())
        .build();

    var updatedMatchMessage = MessageBuilder.withPayload(updatedMatch)
        .setHeader(KafkaHeaders.RECEIVED_KEY, id)
        .build();

    //when
    matchKafkaConsumer.matches().accept(updatedMatchMessage);

    //then
    assertAsync(() -> assertSavedMatchById(updatedMatch));
  }

  private void assertAsync(ThrowingRunnable assertion) {
    Awaitility.await()
        .atMost(TIMEOUT_IN_SECONDS, SECONDS)
        .pollInterval(POLL_INTERVAL_IN_SECONDS, SECONDS)
        .untilAsserted(assertion);
  }

  private void assertSavedMatchById(Match expectedMatch) {
    var savedMatch = matchRepository.findById(expectedMatch.id()).block();
    assertThat(savedMatch).isEqualTo(expectedMatch);
  }
}