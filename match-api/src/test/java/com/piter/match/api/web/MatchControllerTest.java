package com.piter.match.api.web;

import static com.piter.match.api.util.MatchTestData.createMatchList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

import com.piter.api.commons.domain.Match;
import com.piter.match.api.config.MatchApiTestConfig;
import com.piter.match.api.config.SecurityTestConfig;
import com.piter.match.api.repository.MatchRepository;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = MatchController.class)
@Import({MatchApiTestConfig.class, SecurityTestConfig.class})
class MatchControllerTest {

  private static final String BET_USER = "BET_USER";
  private static final List<Match> MATCHES = createMatchList();

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
  void shouldReturnUnauthorized() {
    webClient
        .get()
        .uri("/matches")
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  @WithMockUser
  void shouldReturnForbidden() {
    webClient
        .mutateWith(mockUser())
        .get()
        .uri("/matches")
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  @WithMockUser
  void shouldGetMatchesWithoutOrder() {
    webClient
        .mutateWith(mockUser().authorities(BET_USER))
        .get()
        .uri("/matches")
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
    webClient
        .mutateWith(mockUser().authorities(BET_USER))
        .get()
        .uri("/matches?order=match-time")
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
    webClient
        .mutateWith(mockUser().authorities(BET_USER))
        .get()
        .uri("/matches?order=round-time")
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
    webClient
        .mutateWith(mockUser().authorities(BET_USER))
        .get()
        .uri("/matches/" + id)
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