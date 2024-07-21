package com.piter.match.api.service;

import static com.piter.match.api.util.MatchTestData.createMatchList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.piter.api.commons.domain.Match;
import com.piter.api.commons.domain.MatchResult;
import com.piter.api.commons.domain.MatchRound;
import com.piter.match.api.config.MatchApiTestConfig;
import com.piter.match.api.exception.MatchNotFoundException;
import com.piter.match.api.producer.MatchKafkaProducer;
import com.piter.match.api.repository.MatchRepository;
import com.piter.match.api.util.DbPopulatorUtil;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Tag("EmbeddedMongoDBTest")
@DataMongoTest
@ActiveProfiles("TEST")
@ExtendWith({SpringExtension.class, MockitoExtension.class})
@Import(MatchApiTestConfig.class)
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
  void initDatabase() {
    List<Match> matches = createMatchList();
    DbPopulatorUtil.fillDatabaseIfEmpty(matchRepository, matches);
  }

  @Test
  void shouldGetMatchesWithoutOrder() {
    Flux<Match> matchFlux = matchService.findAll();
    StepVerifier.create(matchFlux)
        .expectNextCount(4)
        .verifyComplete();
  }

  @Test
  void shouldGetMatchesOrderedByRoundTime() {
    Flux<Match> matchFlux = matchService.findAllByOrderByMatchRoundStartTime();
    StepVerifier.create(matchFlux)
        .assertNext(match -> assertThat(match.id()).isEqualTo(1L))
        .assertNext(match -> assertThat(match.id()).isEqualTo(3L))
        .assertNext(match -> assertThat(match.id()).isEqualTo(4L))
        .assertNext(match -> assertThat(match.id()).isEqualTo(2L))
        .verifyComplete();
  }

  @Test
  void shouldGetMatchesOrderedByMatchTime() {
    Flux<Match> matchFlux = matchService.findAllByOrderByMatchStartTime();
    StepVerifier.create(matchFlux)
        .assertNext(match -> assertThat(match.id()).isEqualTo(1L))
        .assertNext(match -> assertThat(match.id()).isEqualTo(2L))
        .assertNext(match -> assertThat(match.id()).isEqualTo(4L))
        .assertNext(match -> assertThat(match.id()).isEqualTo(3L))
        .verifyComplete();
  }

  @Test
  void shouldGetUpcomingMatchesOrderedByMatchTimeDesc() {
    Flux<Match> matchFlux = matchService.findAllUpcomingOrderByStartTimeDesc();
    StepVerifier.create(matchFlux)
        .assertNext(match -> assertThat(match.id()).isEqualTo(1L))
        .assertNext(match -> assertThat(match.id()).isEqualTo(2L))
        .verifyComplete();
  }

  @Test
  void shouldGetUpcomingMatchesOrderedByMatchTimeAsc() {
    Flux<Match> matchFlux = matchService.findAllUpcomingOrderByStartTimeAsc();
    StepVerifier.create(matchFlux)
        .assertNext(match -> assertThat(match.id()).isEqualTo(2L))
        .assertNext(match -> assertThat(match.id()).isEqualTo(1L))
        .verifyComplete();
  }

  @Test
  void shouldGetOngoingMatches() {
    Flux<Match> matchFlux = matchService.findAllOngoing();
    StepVerifier.create(matchFlux)
        .assertNext(match -> assertThat(match.id()).isEqualTo(4L))
        .verifyComplete();
  }

  @Test
  void shouldGetMatchById() {
    var id = 2L;
    Mono<Match> matchMono = matchService.findById(id);
    StepVerifier.create(matchMono)
        .assertNext(match -> assertThat(match.id()).isEqualTo(id))
        .verifyComplete();
  }

  @Test
  @Order(Integer.MAX_VALUE)
  void shouldSaveMatch() {
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
        .assertNext(match -> assertThat(match.id()).isEqualTo(generatedId))
        .verifyComplete();
  }

  private void mockSequenceGeneratorServiceCreateId(long generatedId) {
    when(sequenceGeneratorService.generateSequenceMatchId(Match.SEQUENCE_NAME)).thenReturn(
        Mono.just(generatedId));
  }

  private void mockMatchKafkaProducerSavingInDatabase() {
    when(matchKafkaProducer.sendSaveMatchEvent(any(Match.class))).thenAnswer(
        answer -> answer.getArgument(0, Match.class));
  }

  @Test
  @Order(Integer.MAX_VALUE)
  void shouldUpdateMatch() {
    //given
    var id = 100L;
    var matchToSave = Match.builder()
        .id(id)
        .homeTeam("FC Barcelona")
        .awayTeam("Athletic Bilbao")
        .startTime(LocalDateTime.of(2022, 10, 23, 21, 0, 0))
        .round(new MatchRound("LaLiga round 11", LocalDateTime.of(2022, 10, 22, 14, 0, 0)))
        .build();

    matchRepository.save(matchToSave).block();

    var matchToUpdate = Match.builder()
        .homeTeam(matchToSave.homeTeam())
        .awayTeam(matchToSave.awayTeam())
        .startTime(matchToSave.startTime().plusDays(1))
        .round(matchToSave.round())
        .build();

    mockMatchKafkaProducerSavingInDatabase();

    //when
    Mono<Match> matchMono = matchService.updateMatch(id, matchToUpdate);

    //then
    StepVerifier.create(matchMono)
        .assertNext(match -> assertThat(match.id()).isEqualTo(id))
        .verifyComplete();
  }

  @Test
  void shouldReturnErrorDuringMatchUpdateIfNotFound() {
    //given
    var nonExistingId = 12324L;
    var matchToUpdate = Match.builder()
        .homeTeam("FC Barcelona")
        .awayTeam("Girona")
        .startTime(LocalDateTime.now())
        .build();

    //when
    Mono<Match> matchMono = matchService.updateMatch(nonExistingId, matchToUpdate);

    //then
    StepVerifier.create(matchMono)
        .expectError(MatchNotFoundException.class)
        .verify();
  }

  @Test
  void shouldUpdateMatchResult() {
    //given
    var id = 1L;
    var matchResult = new MatchResult(3, 0);

    mockMatchKafkaProducerSavingInDatabase();

    //when
    Mono<Match> matchMono = matchService.updateMatchResult(id, matchResult);

    //then
    StepVerifier.create(matchMono)
        .assertNext(match -> {
          assertThat(match.id()).isEqualTo(id);
          assertThat(match.result()).isEqualTo(matchResult);
        })
        .verifyComplete();
  }

  @Test
  void shouldReturnErrorDuringMatchResultUpdateIfNotFound() {
    //given
    var nonExistingId = 12324L;
    var matchResult = new MatchResult(3, 0);

    //when
    Mono<Match> matchMono = matchService.updateMatchResult(nonExistingId, matchResult);

    //then
    StepVerifier.create(matchMono)
        .expectError(MatchNotFoundException.class)
        .verify();
  }

  @Test
  void shouldReturnErrorDuringDeletingMatch() {
    //given
    var nonExistingId = 12324L;

    //when
    Mono<Void> voidMono = matchService.deleteMatch(nonExistingId);

    //then
    StepVerifier.create(voidMono)
        .expectError(MatchNotFoundException.class)
        .verify();
  }
}