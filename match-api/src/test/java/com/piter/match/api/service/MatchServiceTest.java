package com.piter.match.api.service;

import static com.piter.match.api.util.MatchTestData.createMatchList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.piter.api.commons.domain.Match;
import com.piter.api.commons.domain.MatchRound;
import com.piter.match.api.config.MatchApiTestConfig;
import com.piter.match.api.exception.MatchNotFoundException;
import com.piter.match.api.producer.MatchKafkaProducer;
import com.piter.match.api.repository.MatchRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@Import(MatchApiTestConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
class MatchServiceTest {

  @Autowired
  private MatchRepository matchRepository;

  @Autowired
  private MatchService matchService;

  @MockBean
  private MatchKafkaProducer matchKafkaProducer;

  @MockBean
  private SequenceGeneratorService sequenceGeneratorService;

  @BeforeEach
  void fillDatabaseIfEmpty() {
    List<Match> existingMatches = matchRepository.findAll()
        .collectList()
        .block();
    if (existingMatches == null || existingMatches.isEmpty()) {
      fillDatabase();
    }
  }

  private void fillDatabase() {
    List<Match> matches = createMatchList();
    matches.forEach(match -> matchRepository.save(match).block());
  }

  @Test
  void shouldGetMatchesWithoutOrder() {
    Flux<Match> matchFlux = matchService.findAll();
    StepVerifier.create(matchFlux)
        .expectNextCount(3)
        .verifyComplete();
  }

  @Test
  void shouldGetMatchesOrderedByRoundTime() {
    Flux<Match> matchFlux = matchService.findAllByOrderByMatchRoundStartTime();
    StepVerifier.create(matchFlux)
        .assertNext(match -> assertThat(match.getId()).isEqualTo(1L))
        .assertNext(match -> assertThat(match.getId()).isEqualTo(3L))
        .assertNext(match -> assertThat(match.getId()).isEqualTo(2L))
        .verifyComplete();
  }

  @Test
  void shouldGetMatchesOrderedByMatchTime() {
    Flux<Match> matchFlux = matchService.findAllByOrderByMatchStartTime();
    StepVerifier.create(matchFlux)
        .assertNext(match -> assertThat(match.getId()).isEqualTo(1L))
        .assertNext(match -> assertThat(match.getId()).isEqualTo(2L))
        .assertNext(match -> assertThat(match.getId()).isEqualTo(3L))
        .verifyComplete();
  }

  @Test
  void shouldGetUpcomingMatchesOrderedByMatchTimeDesc() {
    Flux<Match> matchFlux = matchService.findAllUpcomingOrderByStartTimeDesc();
    StepVerifier.create(matchFlux)
        .assertNext(match -> assertThat(match.getId()).isEqualTo(1L))
        .assertNext(match -> assertThat(match.getId()).isEqualTo(2L))
        .verifyComplete();
  }

  @Test
  void shouldGetUpcomingMatchesOrderedByMatchTimeAsc() {
    Flux<Match> matchFlux = matchService.findAllUpcomingOrderByStartTimeAsc();
    StepVerifier.create(matchFlux)
        .assertNext(match -> assertThat(match.getId()).isEqualTo(2L))
        .assertNext(match -> assertThat(match.getId()).isEqualTo(1L))
        .verifyComplete();
  }

  @Test
  void shouldGetMatchById() {
    var id = 2L;
    Mono<Match> matchMono = matchService.findById(id);
    StepVerifier.create(matchMono)
        .assertNext(match -> assertThat(match.getId()).isEqualTo(id))
        .verifyComplete();
  }

  @Test
  @Order(Integer.MAX_VALUE)
  void shouldSaveMatchWithId() {
    //given
    var id = 10L;
    var matchToSave = Match.builder()
        .id(id)
        .homeTeam("FC Barcelona")
        .awayTeam("Athletic Bilbao")
        .startTime(LocalDateTime.of(2022, 10, 23, 21, 0, 0))
        .round(new MatchRound("LaLiga round 11", LocalDateTime.of(2022, 10, 22, 14, 0, 0)))
        .build();

    mockMatchKafkaProducerSavingInDatabase();

    //when
    Mono<Match> matchMono = matchService.saveMatch(matchToSave);

    //then
    StepVerifier.create(matchMono)
        .assertNext(match -> assertThat(match.getId()).isEqualTo(id))
        .verifyComplete();
    assertSavedMatchById(matchToSave);
  }

  private void mockMatchKafkaProducerSavingInDatabase() {
    when(matchKafkaProducer.sendSaveMatchEvent(any(Match.class))).thenAnswer(answer -> {
      Match matchArgument = answer.getArgument(0, Match.class);
      matchRepository.save(matchArgument).block();
      return matchArgument;
    });
  }

  private void assertSavedMatchById(Match expectedMatch) {
    var savedMatch = matchRepository.findById(expectedMatch.getId()).block();
    assertThat(savedMatch).isEqualTo(expectedMatch);
  }

  @Test
  @Order(Integer.MAX_VALUE)
  void shouldSaveMatchWithoutId() {
    //given
    var matchToSave = Match.builder()
        .homeTeam("FC Barcelona")
        .awayTeam("Athletic Bilbao")
        .startTime(LocalDateTime.of(2022, 10, 23, 21, 0, 0))
        .round(new MatchRound("LaLiga round 11", LocalDateTime.of(2022, 10, 22, 14, 0, 0)))
        .build();

    var generatedId = 99L;

    mockSequenceGeneratorServiceCreateId(generatedId);
    mockMatchKafkaProducerSavingInDatabase();

    //when
    Mono<Match> matchMono = matchService.saveMatch(matchToSave);

    //then
    StepVerifier.create(matchMono)
        .assertNext(match -> assertThat(match.getId()).isEqualTo(generatedId))
        .verifyComplete();
    assertSavedMatchWithoutId(generatedId, matchToSave);
  }

  private void mockSequenceGeneratorServiceCreateId(long generatedId) {
    when(sequenceGeneratorService.generateSequenceMatchId(Match.SEQUENCE_NAME)).thenReturn(Mono.just(generatedId));
  }

  private void assertSavedMatchWithoutId(Long generatedId, Match expectedMatch) {
    var savedMatch = matchRepository.findById(generatedId).block();
    assertThat(savedMatch).usingRecursiveComparison()
        .ignoringFields("id")
        .isEqualTo(expectedMatch);
  }

  @Test
  void shouldReturnErrorDuringDeletingMatch() {
    var nonExistingId = 12324L;
    Mono<Void> voidMono = matchService.deleteMatch(nonExistingId);
    StepVerifier.create(voidMono)
        .expectError(MatchNotFoundException.class)
        .verify();
  }
}