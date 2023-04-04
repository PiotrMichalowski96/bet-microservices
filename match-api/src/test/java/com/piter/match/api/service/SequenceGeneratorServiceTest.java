package com.piter.match.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.piter.api.commons.domain.DatabaseSequence;
import com.piter.api.commons.domain.Match;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class SequenceGeneratorServiceTest {

  @Mock
  private ReactiveMongoOperations mongoOperations;

  @InjectMocks
  private SequenceGeneratorService sequenceGeneratorService;

  @Test
  void shouldGenerateAndIncreasingMatchId() {

    mockRetrievingFromMongoDB();

    Long defaultIdValue = 1L;

    Mono<Long> matchIdMono = sequenceGeneratorService.generateSequenceMatchId(Match.SEQUENCE_NAME);
    StepVerifier.create(matchIdMono)
        .assertNext(id -> assertThat(id).isEqualTo(defaultIdValue))
        .verifyComplete();

    Mono<Long> increasedMatchIdMono = sequenceGeneratorService.generateSequenceMatchId(
        Match.SEQUENCE_NAME);
    StepVerifier.create(increasedMatchIdMono)
        .assertNext(id -> assertThat(id).isEqualTo(defaultIdValue + 1))
        .verifyComplete();
  }

  private void mockRetrievingFromMongoDB() {
    when(mongoOperations.findAndModify(any(), any(), any(), eq(DatabaseSequence.class)))
        .thenReturn(Mono.just(new DatabaseSequence("id", 1L)))
        .thenReturn(Mono.just(new DatabaseSequence("id", 2L)));
  }
}