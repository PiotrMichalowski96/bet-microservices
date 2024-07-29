package com.piter.match.api.service;

import static com.piter.match.api.util.MatchTestData.createMatchList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assumptions.assumeThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.piter.api.commons.event.MatchEvent;
import com.piter.api.commons.model.Match;
import com.piter.api.commons.model.MatchResult;
import com.piter.api.commons.model.MatchRound;
import com.piter.match.api.config.MatchApiTestConfig;
import com.piter.match.api.exception.MatchNotFoundException;
import com.piter.match.api.producer.MatchKafkaProducer;
import com.piter.match.api.repository.MatchRepository;
import com.piter.match.api.util.DbPopulatorUtil;
import com.piter.match.api.web.RequestParams;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cache.CacheManager;
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

  @Autowired
  private CacheManager cacheManager;

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
  void shouldGetMatches() {
    var requestParams = new RequestParams(null, null, RequestParams.Order.MATCH_TIME_DESC);
    Flux<Match> matchFlux = matchService.findAllBy(requestParams);
    StepVerifier.create(matchFlux)
        .expectNextCount(4)
        .verifyComplete();
  }

  @ParameterizedTest
  @CsvSource({"0, 1, 2", "1, 4, 3"})
  void shouldGetMatchesPages(int page, long expectedFirstMatchId, int expectedSecondMatchId) {
    int pageSize = 2;
    var requestParams = new RequestParams(page, pageSize, RequestParams.Order.MATCH_TIME_DESC);
    Flux<Match> matchFlux = matchService.findAllBy(requestParams);
    StepVerifier.create(matchFlux)
        .assertNext(match -> assertThat(match.id()).isEqualTo(expectedFirstMatchId))
        .assertNext(match -> assertThat(match.id()).isEqualTo(expectedSecondMatchId))
        .verifyComplete();
  }

  @Test
  void shouldGetMatchesOrderedByRoundTime() {
    var requestParams = new RequestParams(null, null, RequestParams.Order.ROUND_TIME_DESC);
    Flux<Match> matchFlux = matchService.findAllBy(requestParams);
    StepVerifier.create(matchFlux)
        .assertNext(match -> assertThat(match.id()).isEqualTo(1L))
        .assertNext(match -> assertThat(match.id()).isEqualTo(3L))
        .assertNext(match -> assertThat(match.id()).isEqualTo(4L))
        .assertNext(match -> assertThat(match.id()).isEqualTo(2L))
        .verifyComplete();
  }

  @Test
  void shouldGetMatchesOrderedByMatchTime() {
    var requestParams = new RequestParams(null, null, RequestParams.Order.MATCH_TIME_DESC);
    Flux<Match> matchFlux = matchService.findAllBy(requestParams);
    StepVerifier.create(matchFlux)
        .assertNext(match -> assertThat(match.id()).isEqualTo(1L))
        .assertNext(match -> assertThat(match.id()).isEqualTo(2L))
        .assertNext(match -> assertThat(match.id()).isEqualTo(4L))
        .assertNext(match -> assertThat(match.id()).isEqualTo(3L))
        .verifyComplete();
  }

  @Test
  void shouldGetUpcomingMatchesOrderedByMatchTimeDesc() {
    var requestParams = new RequestParams(null, null, RequestParams.Order.MATCH_TIME_DESC);
    Flux<Match> matchFlux = matchService.findAllUpcomingBy(requestParams);
    StepVerifier.create(matchFlux)
        .assertNext(match -> assertThat(match.id()).isEqualTo(1L))
        .assertNext(match -> assertThat(match.id()).isEqualTo(2L))
        .verifyComplete();
  }

  @Test
  void shouldGetUpcomingMatchesOrderedByMatchTimeAsc() {
    var requestParams = new RequestParams(null, null, RequestParams.Order.MATCH_TIME_ASC);
    Flux<Match> matchFlux = matchService.findAllUpcomingBy(requestParams);
    StepVerifier.create(matchFlux)
        .assertNext(match -> assertThat(match.id()).isEqualTo(2L))
        .assertNext(match -> assertThat(match.id()).isEqualTo(1L))
        .verifyComplete();
  }

  @Test
  void shouldGetOngoingMatches() {
    var requestParams = new RequestParams(null, null, RequestParams.Order.MATCH_TIME_DESC);
    Flux<Match> matchFlux = matchService.findAllOngoingBy(requestParams);
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
    when(matchKafkaProducer.sendSaveMatchEvent(any(MatchEvent.class))).thenAnswer(
        answer -> answer.getArgument(0, MatchEvent.class));
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

  @Test
  void shouldCacheMatchById() {
    //given
    var id = 1L;

    //when
    Mono<Match> matchMono = matchService.findById(id);

    //then
    StepVerifier.create(matchMono)
        .assertNext(match -> assertThat(match.id()).isEqualTo(id))
        .verifyComplete();
    assertThat(cacheManager.getCache("matches")).isNotNull();
    assertThat(cacheManager.getCache("matches").get(id)).isNotNull();
  }

  @Test
  void shouldRemoveCacheAfterUpdate() {
    //given
    var id = 1L;
    assumeMatchIsCached(id);

    var matchToUpdate = Match.builder()
        .homeTeam("FC Barcelona")
        .awayTeam("Girona")
        .startTime(LocalDateTime.now())
        .build();

    mockMatchKafkaProducerSavingInDatabase();

    //when
    Mono<Match> matchMono = matchService.updateMatch(id, matchToUpdate);

    //then
    StepVerifier.create(matchMono)
        .assertNext(match -> assertThat(match.id()).isEqualTo(id))
        .verifyComplete();
    assertThat(cacheManager.getCache("matches")).isNotNull();
    assertThat(cacheManager.getCache("matches").get(id)).isNull();
  }

  private void assumeMatchIsCached(long id) {
    matchService.findById(id).block();
    assumeThat(cacheManager.getCache("matches").get(id)).isNotNull();
  }
}