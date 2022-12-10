package com.piter.match.api.web;

import static com.piter.match.api.util.MatchTestData.createMatchList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOpaqueToken;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

import com.piter.api.commons.domain.Match;
import com.piter.api.commons.domain.MatchRound;
import com.piter.match.api.config.MatchApiTestConfig;
import com.piter.match.api.config.SecurityTestConfig;
import com.piter.match.api.producer.MatchKafkaProducer;
import com.piter.match.api.repository.MatchRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClientConfigurer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = MatchController.class, properties = "spring.cloud.config.enabled=false")
@Import({MatchApiTestConfig.class, SecurityTestConfig.class})
class MatchControllerTest {

  private static final String BET_ADMIN = "BET_ADMIN";
  private static final List<Match> MATCHES = createMatchList();

  @MockBean
  private ReactiveMongoOperations reactiveMongoOperations;

  @MockBean
  private MatchRepository matchRepository;

  @MockBean
  private MatchKafkaProducer matchKafkaProducer;

  @Autowired
  private WebTestClient webClient;

  @BeforeEach
  void initMocks() {
    mockFindAll();
    mockFindOrderedByRoundTime();
    mockFindOrderedByMatchTimeDesc();
    mockFindOrderedByMatchTimeAsc();
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

  private void mockFindOrderedByMatchTimeDesc() {
    List<Match> matchesOrderedByMatchTime = MATCHES.stream()
        .sorted(Comparator.comparing(Match::getStartTime).reversed())
        .toList();
    when(matchRepository.findAllByOrderByStartTimeDesc()).thenReturn(
        Flux.fromIterable(matchesOrderedByMatchTime));
  }

  private void mockFindOrderedByMatchTimeAsc() {
    List<Match> matchesOrderedByMatchTime = MATCHES.stream()
        .sorted(Comparator.comparing(Match::getStartTime))
        .toList();
    when(matchRepository.findAllByOrderByStartTimeAsc()).thenReturn(
        Flux.fromIterable(matchesOrderedByMatchTime));
  }


  @Test
  @WithMockUser
  void shouldReturnForbidden() {
    webClient
        .mutateWith(mockUser())
        .delete()
        .uri("/matches/1")
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  @WithMockUser
  void shouldGetMatchesWithoutOrder() {
    webClient
        .mutateWith(mockUser().authorities(BET_ADMIN))
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
        .mutateWith(mockUser().authorities(BET_ADMIN))
        .get()
        .uri("/matches?order=match-time")
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
  void shouldGetMatchesOrderedByRoundTime() {
    webClient
        .mutateWith(mockUser().authorities(BET_ADMIN))
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
  void shouldGetUpcomingMatchesOrderedByMatchTimeDesc() {
    webClient
        .mutateWith(mockUser().authorities(BET_ADMIN))
        .get()
        .uri("/matches/upcoming?startTime=desc")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Match.class)
        .value(matches -> {
          assertThat(matches.get(0).getId()).isEqualTo(1L);
          assertThat(matches.get(1).getId()).isEqualTo(2L);
        });
  }

  @Test
  void shouldGetUpcomingMatchesOrderedByMatchTimeAsc() {
    webClient
        .mutateWith(mockUser().authorities(BET_ADMIN))
        .get()
        .uri("/matches/upcoming?startTime=asc")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Match.class)
        .value(matches -> {
          assertThat(matches.get(0).getId()).isEqualTo(2L);
          assertThat(matches.get(1).getId()).isEqualTo(1L);
        });
  }

  @Test
  void shouldGetMatchById() {
    var id = 3L;
    mockFindById(id);
    webClient
        .mutateWith(mockUser().authorities(BET_ADMIN))
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

  @Test
  @WithMockUser
  void shouldSaveMatch() {
    var match = Match.builder()
        .id(9876L)
        .homeTeam("FC Barcelona")
        .awayTeam("Athletic Bilbao")
        .startTime(LocalDateTime.of(2022, 10, 23, 21, 0, 0))
        .round(new MatchRound("LaLiga round 11", LocalDateTime.of(2022, 10, 22, 14, 0, 0)))
        .build();

    mockSavingMatchEventByProducer(match);

    webClient
        .mutateWith(mockBearerToken())
        .mutateWith(csrf())
        .post()
        .uri("/matches")
        .body(Mono.just(match), Match.class)
        .exchange()
        .expectStatus().isOk();
  }

  private void mockSavingMatchEventByProducer(Match match) {
    when(matchKafkaProducer.sendSaveMatchEvent(any(Match.class))).thenReturn(match);
  }

  private WebTestClientConfigurer mockBearerToken() {
    Consumer<Map<String, Object>> attributesConsumer = attributes -> {
      attributes.put("name", "Arya Stark");
      attributes.put("username", "needle");
    };
    return mockOpaqueToken()
        .authorities(new SimpleGrantedAuthority(BET_ADMIN))
        .attributes(attributesConsumer);
  }

  @Test
  void shouldNotSaveBecauseMatchIsInvalid() {
    var invalidMatch = Match.builder().build();
    webClient
        .mutateWith(mockBearerToken())
        .mutateWith(csrf())
        .post()
        .uri("/matches")
        .body(Mono.just(invalidMatch), Match.class)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void shouldNotSaveBecauseUserIsNotAnAdmin() {
    var match = Match.builder().build();
    webClient
        .mutateWith(csrf())
        .post()
        .uri("/matches")
        .body(Mono.just(match), Match.class)
        .exchange()
        .expectStatus().isUnauthorized();
  }
}