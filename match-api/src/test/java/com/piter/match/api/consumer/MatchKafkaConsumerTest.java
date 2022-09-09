package com.piter.match.api.consumer;

import static org.assertj.core.api.Assertions.assertThat;

import com.piter.match.api.config.MatchApiTestConfig;
import com.piter.match.api.domain.Match;
import com.piter.match.api.domain.MatchResult;
import com.piter.match.api.domain.MatchRound;
import com.piter.match.api.repository.MatchRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest
@ActiveProfiles("TEST")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MatchApiTestConfig.class)
class MatchKafkaConsumerTest {

  @Autowired
  private MatchKafkaConsumer matchKafkaConsumer;

  @Autowired
  private MatchRepository matchRepository;

  @Test
  void shouldInsertMatch() {
    //given
    var messageConsumer = matchKafkaConsumer.matches();
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
    messageConsumer.accept(matchMessage);

    //then
    matchRepository.findAll()
        .filter(m -> homeTeam.equals(m.getHomeTeam()) && awayTeam.equals(m.getAwayTeam()))
        .subscribe(
            savedMatch -> assertThat(savedMatch).isEqualTo(match)
        );
  }

  @Test
  void shouldUpdateMatch() {
    //given
    var messageConsumer = matchKafkaConsumer.matches();
    var id = 99L;
    var match = Match.builder()
        .id(id)
        .homeTeam("FC Barcelona")
        .awayTeam("Real Madrid")
        .startTime(LocalDateTime.of(2022, 2, 17, 21, 0, 0))
        .result(new MatchResult(4, 0))
        .round(new MatchRound("LaLiga round 30", LocalDateTime.of(2022, 2, 14, 21, 0, 0)))
        .build();

    matchRepository.save(match);

    var updatedMatch = Match.builder()
        .id(match.getId())
        .homeTeam(match.getHomeTeam())
        .awayTeam(match.getAwayTeam())
        .startTime(match.getStartTime())
        .result(new MatchResult(4, 2))
        .round(match.getRound())
        .build();

    var matchMessage = MessageBuilder.withPayload(match)
        .setHeader(KafkaHeaders.RECEIVED_MESSAGE_KEY, id)
        .build();

    //when
    messageConsumer.accept(matchMessage);

    //then
    matchRepository.findById(id).subscribe(
        savedMatch -> assertThat(savedMatch).isEqualTo(updatedMatch)
    );
  }
}