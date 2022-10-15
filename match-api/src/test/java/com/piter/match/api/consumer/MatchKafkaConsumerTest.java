package com.piter.match.api.consumer;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;

import com.piter.api.commons.domain.Match;
import com.piter.api.commons.domain.MatchResult;
import com.piter.api.commons.domain.MatchRound;
import com.piter.match.api.config.MatchApiTestConfig;
import com.piter.match.api.repository.MatchRepository;
import java.time.LocalDateTime;
import java.util.function.Predicate;
import org.awaitility.Awaitility;
import org.awaitility.core.ThrowingRunnable;
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

@DataMongoTest
@ActiveProfiles("TEST")
@ExtendWith(SpringExtension.class)
@Import(MatchApiTestConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class MatchKafkaConsumerTest {

  private static final int TIMEOUT_IN_SECONDS = 20;
  private static final int POLL_INTERVAL_IN_SECONDS = 2;

  @Autowired
  private MatchKafkaConsumer matchKafkaConsumer;

  @Autowired
  private MatchRepository matchRepository;

  @Test
  void shouldInsertMatch() {
    //given
    var homeTeam = "Atletico Madrid";
    var awayTeam = "Valencia";
    var match = Match.builder()
        .homeTeam(homeTeam)
        .awayTeam(awayTeam)
        .startTime(LocalDateTime.of(2022, 2, 15, 21, 0, 0))
        .result(new MatchResult(2, 2))
        .round(new MatchRound("LaLiga round 30", LocalDateTime.of(2022, 2, 14, 21, 0, 0)))
        .build();

    var matchMessage = MessageBuilder.withPayload(match).build();

    //when
    matchKafkaConsumer.matches().accept(matchMessage);

    //then
    Predicate<Match> findMatchByTeams = m -> homeTeam.equals(m.getHomeTeam()) && awayTeam.equals(m.getAwayTeam());
    assertAsync(() -> assertSavedMatch(findMatchByTeams, match));
  }

  private void assertAsync(ThrowingRunnable assertion) {
    Awaitility.await()
        .atMost(TIMEOUT_IN_SECONDS, SECONDS)
        .pollInterval(POLL_INTERVAL_IN_SECONDS, SECONDS)
        .untilAsserted(assertion);
  }

  private void assertSavedMatch(Predicate<Match> findMatchPredicate, Match expectedMatch) {
    var savedMatch = matchRepository.findAll()
        .filter(findMatchPredicate)
        .blockFirst();
    assertThat(savedMatch).usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(expectedMatch);
  }

  @Test
  void shouldUpdateMatch() {
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
        .id(match.getId())
        .homeTeam(match.getHomeTeam())
        .awayTeam(match.getAwayTeam())
        .startTime(match.getStartTime())
        .result(new MatchResult(4, 2))
        .round(match.getRound())
        .build();

    var updatedMatchMessage = MessageBuilder.withPayload(updatedMatch)
        .setHeader(KafkaHeaders.RECEIVED_MESSAGE_KEY, id)
        .build();

    //when
    matchKafkaConsumer.matches().accept(updatedMatchMessage);

    //then
    assertAsync(() -> assertSavedMatch(id, updatedMatch));
  }

  private void assertSavedMatch(Long id, Match expectedMatch) {
    var savedMatch = matchRepository.findById(id).block();
    assertThat(savedMatch).isEqualTo(expectedMatch);
  }
}