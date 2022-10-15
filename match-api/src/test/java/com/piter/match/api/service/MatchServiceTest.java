package com.piter.match.api.service;

import static com.piter.match.api.util.MatchTestData.createMatchList;
import static org.assertj.core.api.Assertions.assertThat;

import com.piter.api.commons.domain.Match;
import com.piter.match.api.config.MatchApiTestConfig;
import com.piter.match.api.repository.MatchRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
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
@ExtendWith(SpringExtension.class)
@Import(MatchApiTestConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class MatchServiceTest {

  @Autowired
  private MatchRepository matchRepository;

  @Autowired
  private MatchService matchService;

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
        .assertNext(match -> assertThat(match.getId()).isEqualTo(3L))
        .assertNext(match -> assertThat(match.getId()).isEqualTo(2L))
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
}