package com.piter.match.api.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.piter.match.api.config.MatchApiTestConfig;
import com.piter.match.api.domain.Match;
import com.piter.match.api.domain.MatchResult;
import com.piter.match.api.domain.MatchRound;
import com.piter.match.api.repository.MatchRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = MatchRouterConfig.class)
@Import(MatchApiTestConfig.class)
class MatchRouterTest {

  private static final List<Match> MATCHES = List.of(
      Match.builder()
          .id(1L)
          .homeTeam("FC Barcelona")
          .awayTeam("Real Madrid")
          .startTime(LocalDateTime.of(2022, 2, 17, 21, 0, 0))
          .result(new MatchResult(4, 0))
          .round(new MatchRound("LaLiga round 30", LocalDateTime.of(2022, 2, 14, 21, 0, 0)))
          .build(),
      Match.builder()
          .id(2L)
          .homeTeam("Sevilla")
          .awayTeam("Athletic Bilbao")
          .startTime(LocalDateTime.of(2022, 2, 8, 19, 0, 0))
          .result(new MatchResult(2, 1))
          .round(new MatchRound("LaLiga round 29", LocalDateTime.of(2022, 2, 7, 21, 0, 0)))
          .build(),
      Match.builder()
          .id(3L)
          .homeTeam("Atletico Madrid")
          .awayTeam("Valencia")
          .startTime(LocalDateTime.of(2022, 2, 15, 21, 0, 0))
          .result(new MatchResult(2, 2))
          .round(new MatchRound("LaLiga round 30", LocalDateTime.of(2022, 2, 14, 21, 0, 0)))
          .build()
  );

  @MockBean
  private ReactiveMongoOperations reactiveMongoOperations;

  @MockBean
  private MatchRepository matchRepository;

  @Autowired
  private WebTestClient webClient;

  @BeforeEach
  void initMocks() {
    mockFindAll();
    mockFindOrderedByRoundTime();
    mockFindOrderedByMatchTime();
  }

  private void mockFindAll() {
    when(matchRepository.findAll()).thenReturn(Flux.fromIterable(MATCHES));
  }

  private void mockFindOrderedByRoundTime() {
    Comparator<Match> roundTimeComparator = Comparator.comparing(
        match -> match.getRound().getStartTime());
    List<Match> matchesOrderedByRoundTime = MATCHES.stream()
        .sorted(roundTimeComparator.reversed())
        .toList();
    when(matchRepository.findAllByOrderByRoundStartTimeDesc()).thenReturn(
        Flux.fromIterable(matchesOrderedByRoundTime));
  }

  private void mockFindOrderedByMatchTime() {
    List<Match> matchesOrderedByMatchTime = MATCHES.stream()
        .sorted(Comparator.comparing(Match::getStartTime).reversed())
        .toList();
    when(matchRepository.findAllByOrderByStartTimeDesc()).thenReturn(
        Flux.fromIterable(matchesOrderedByMatchTime));
  }

  @Test
  void shouldGetMatchesWithoutOrder() {
    webClient.get()
        .uri("/match")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Match.class)
        .value(matches -> {
          assertThat(matches.get(0).getId()).isEqualTo(1L);
          assertThat(matches.get(1).getId()).isEqualTo(2L);
          assertThat(matches.get(2).getId()).isEqualTo(3L);
        });
  }

  @Test
  void shouldGetMatchesOrderedByMatchTime() {
    webClient.get()
        .uri("/match?order=match-time")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Match.class)
        .value(matches -> {
          assertThat(matches.get(0).getId()).isEqualTo(1L);
          assertThat(matches.get(1).getId()).isEqualTo(3L);
          assertThat(matches.get(2).getId()).isEqualTo(2L);
        });
  }

  @Test
  void shouldGetMatchesOrderedByRoundTime() {
    webClient.get()
        .uri("/match?order=round-time")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Match.class)
        .value(matches -> {
          assertThat(matches.get(0).getId()).isEqualTo(1L);
          assertThat(matches.get(1).getId()).isEqualTo(3L);
          assertThat(matches.get(2).getId()).isEqualTo(2L);
        });
  }

  @Test
  void shouldGetMatchById() {
    var id = 3L;
    mockFindById(id);
    webClient.get()
        .uri("/match/" + id)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Match.class)
        .value(match -> assertThat(match.getId()).isEqualTo(id));
  }

  private void mockFindById(Long id) {
    Match match = MATCHES.stream()
        .filter(m -> Objects.equals(id, m.getId()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Match with this id does not exist"));
    when(matchRepository.findById(id)).thenReturn(Mono.just(match));
  }
}