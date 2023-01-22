package com.piter.match.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.piter.api.commons.domain.Match;
import com.piter.match.api.config.MatchApiTestConfig;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Tag("EmbeddedMongoDBTest")
@DataMongoTest
@ActiveProfiles("TEST")
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@Import(MatchApiTestConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class SequenceGeneratorServiceTest {

  @Autowired
  private SequenceGeneratorService sequenceGeneratorService;

  @Test
  void shouldGenerateAndIncreasingMatchId() {
    Long defaultIdValue = 1L;

    Mono<Long> matchIdMono = sequenceGeneratorService.generateSequenceMatchId(Match.SEQUENCE_NAME);
    StepVerifier.create(matchIdMono)
        .assertNext(id -> assertThat(id).isEqualTo(defaultIdValue))
        .verifyComplete();

    Mono<Long> increasedMatchIdMono = sequenceGeneratorService.generateSequenceMatchId(Match.SEQUENCE_NAME);
    StepVerifier.create(increasedMatchIdMono)
        .assertNext(id -> assertThat(id).isEqualTo(defaultIdValue + 1))
        .verifyComplete();
  }
}