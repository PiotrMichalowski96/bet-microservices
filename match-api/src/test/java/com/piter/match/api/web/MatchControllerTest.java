package com.piter.match.api.web;

import static com.piter.match.api.util.MatchTestData.createMatchList;
import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOpaqueToken;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

import com.piter.api.commons.event.MatchEvent;
import com.piter.api.commons.model.Match;
import com.piter.match.api.config.MatchApiTestConfig;
import com.piter.match.api.config.SecurityTestConfig;
import com.piter.match.api.producer.MatchKafkaProducer;
import com.piter.match.api.repository.MatchRepository;
import com.piter.match.api.service.SequenceGeneratorService;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Order;
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
  private static final List<Match> PERSISTED_MATCHES = createMatchList();

  @MockBean
  private MatchRepository matchRepository;

  @MockBean
  private MatchKafkaProducer matchKafkaProducer;

  @MockBean
  private SequenceGeneratorService sequenceGeneratorService;

  @Autowired
  private WebTestClient webClient;

  @BeforeEach
  void initMocks() {
    mockFindAll();
  }

  private void mockFindAll() {
    when(matchRepository.findAllBy(any(Pageable.class))).thenAnswer(answer -> {
      Pageable pageable = answer.getArgument(0, Pageable.class);
      Order order = pageable.getSort()
          .stream()
          .findFirst()
          .orElseThrow();
      if (order.getProperty().equals("round.startTime")) {
        return Flux.fromIterable(matchesOrderByRound(order.isDescending()));
      } else if (order.getProperty().equals("startTime")) {
        return Flux.fromIterable(matchesOrderByMatchTime(order.isDescending()));
      } else {
        return Flux.fromIterable(PERSISTED_MATCHES);
      }
    });
  }

  private List<Match> matchesOrderByRound(boolean isReversed) {
    Comparator<Match> roundTimeComparator = Comparator.comparing(m -> m.round().startTime());
    return PERSISTED_MATCHES.stream()
        .sorted(isReversed ? roundTimeComparator.reversed() : roundTimeComparator)
        .toList();
  }

  private List<Match> matchesOrderByMatchTime(boolean isReversed) {
    Comparator<Match> matchTimeComparator = Comparator.comparing(Match::startTime);
    return PERSISTED_MATCHES.stream()
        .sorted(isReversed ? matchTimeComparator.reversed() : matchTimeComparator)
        .toList();
  }

  private void mockFindUpcomingMatchesBy(boolean isReversed) {
    when(matchRepository.findByStartTimeAfter(any(LocalDateTime.class), any(Pageable.class)))
        .thenReturn(Flux.fromIterable(matchesOrderByMatchTime(isReversed)
            .stream()
            .filter(m -> m.startTime().isAfter(now()))
            .toList()
        ));
  }

  private void mockOngoingMatches() {
    when(matchRepository.findByStartTimeBeforeAndResultIsNull(any(LocalDateTime.class), any(Pageable.class)))
        .thenReturn(Flux.fromIterable(PERSISTED_MATCHES.stream()
            .filter(m -> m.startTime().isBefore(now()))
            .filter(m -> m.result() == null)
            .toList()
        ));
  }

  private void mockFindById(Long id) {
    Match match = PERSISTED_MATCHES.stream()
        .filter(m -> Objects.equals(id, m.id()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Match with this id does not exist"));
    when(matchRepository.findById(id)).thenReturn(Mono.just(match));
  }

  private void mockNotFoundById(long nonExistingId) {
    when(matchRepository.findById(nonExistingId)).thenReturn(Mono.empty());
  }

  private void mockSequenceGeneratorServiceCreateId() {
    var randomId = new Random().nextLong();
    when(sequenceGeneratorService.generateSequenceMatchId(Match.SEQUENCE_NAME)).thenReturn(
        Mono.just(randomId));
  }

  private void mockSavingMatchEventByProducer() {
    when(matchKafkaProducer.sendSaveMatchEvent(any(MatchEvent.class))).thenAnswer(
        answer -> answer.getArgument(0, MatchEvent.class));
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
  @WithMockUser
  void shouldReturnForbidden() {
    webClient
        .mutateWith(mockUser())
        .delete()
        .uri("/api/v2/matches/1")
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  @WithMockUser
  void shouldGetMatchesWithoutOrder() {
    webClient
        .mutateWith(mockUser().authorities(BET_ADMIN))
        .get()
        .uri("/api/v2/matches")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(MatchResponse.class)
        .value(matches -> {
          assertThat(matches.get(0).id()).isEqualTo(1L);
          assertThat(matches.get(1).id()).isEqualTo(2L);
          assertThat(matches.get(2).id()).isEqualTo(4L);
        });
  }

  @Test
  void shouldGetMatchesOrderedByMatchTime() {
    webClient
        .mutateWith(mockUser().authorities(BET_ADMIN))
        .get()
        .uri("/api/v2/matches?order=match-time-desc")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(MatchResponse.class)
        .value(matches -> {
          assertThat(matches.get(0).id()).isEqualTo(1L);
          assertThat(matches.get(1).id()).isEqualTo(2L);
          assertThat(matches.get(2).id()).isEqualTo(4L);
          assertThat(matches.get(3).id()).isEqualTo(3L);
        });
  }

  @Test
  void shouldGetMatchesOrderedByRoundTime() {
    webClient
        .mutateWith(mockUser().authorities(BET_ADMIN))
        .get()
        .uri("/api/v2/matches?order=round-time-desc")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(MatchResponse.class)
        .value(matches -> {
          assertThat(matches.get(0).id()).isEqualTo(1L);
          assertThat(matches.get(1).id()).isEqualTo(3L);
          assertThat(matches.get(2).id()).isEqualTo(4L);
          assertThat(matches.get(3).id()).isEqualTo(2L);
        });
  }

  @Test
  void shouldGetUpcomingMatchesOrderedByMatchTimeDesc() {
    mockFindUpcomingMatchesBy(true);
    webClient
        .mutateWith(mockUser().authorities(BET_ADMIN))
        .get()
        .uri("/api/v2/matches/upcoming?order=match-time-desc")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(MatchResponse.class)
        .value(matches -> {
          assertThat(matches.get(0).id()).isEqualTo(1L);
          assertThat(matches.get(1).id()).isEqualTo(2L);
        });
  }

  @Test
  void shouldGetUpcomingMatchesOrderedByMatchTimeAsc() {
    mockFindUpcomingMatchesBy(false);
    webClient
        .mutateWith(mockUser().authorities(BET_ADMIN))
        .get()
        .uri("/api/v2/matches/upcoming?order=match-time-asc")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(MatchResponse.class)
        .value(matches -> {
          assertThat(matches.get(0).id()).isEqualTo(2L);
          assertThat(matches.get(1).id()).isEqualTo(1L);
        });
  }

  @Test
  void shouldGetOngoingMatches() {
    mockOngoingMatches();
    webClient
        .mutateWith(mockUser().authorities(BET_ADMIN))
        .get()
        .uri("/api/v2/matches/ongoing")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(MatchResponse.class)
        .value(matches -> {
          assertThat(matches.get(0).id()).isEqualTo(4L);
        });
  }

  @Test
  void shouldGetMatchById() {
    var id = 3L;
    mockFindById(id);
    webClient
        .mutateWith(mockUser().authorities(BET_ADMIN))
        .get()
        .uri("/api/v2/matches/" + id)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Match.class)
        .value(match -> assertThat(match.id()).isEqualTo(id));
  }

  @Test
  @WithMockUser
  void shouldSaveMatch() {
    var matchRequest = MatchRequest.builder()
        .homeTeam("FC Barcelona")
        .awayTeam("Athletic Bilbao")
        .startTime(now())
        .round(new MatchRound("LaLiga round 11", now()))
        .build();

    mockSequenceGeneratorServiceCreateId();
    mockSavingMatchEventByProducer();

    webClient
        .mutateWith(mockBearerToken())
        .mutateWith(csrf())
        .post()
        .uri("/api/v2/matches")
        .body(Mono.just(matchRequest), MatchRequest.class)
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void shouldNotSaveBecauseMatchIsInvalid() {
    var invalidMatchRequest = MatchRequest.builder().build();
    webClient
        .mutateWith(mockBearerToken())
        .mutateWith(csrf())
        .post()
        .uri("/api/v2/matches")
        .body(Mono.just(invalidMatchRequest), MatchRequest.class)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void shouldNotSaveBecauseUserIsNotAnAdmin() {
    var matchRequest = MatchRequest.builder().build();
    webClient
        .mutateWith(csrf())
        .post()
        .uri("/api/v2/matches")
        .body(Mono.just(matchRequest), MatchRequest.class)
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  void shouldUpdateMatch() {
    var id = 1L;
    var matchRequest = MatchRequest.builder()
        .homeTeam("FC Barcelona")
        .awayTeam("Athletic Bilbao")
        .startTime(now())
        .round(new MatchRound("LaLiga round 11", now()))
        .build();

    mockFindById(id);
    mockSavingMatchEventByProducer();

    webClient
        .mutateWith(csrf())
        .put()
        .uri("/api/v2/matches/" + id)
        .body(Mono.just(matchRequest), MatchRequest.class)
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void shouldNotUpdateMatchBecauseNotFound() {
    var nonExistingId = 12324L;
    var matchRequest = MatchRequest.builder()
        .homeTeam("FC Barcelona")
        .awayTeam("Girona")
        .startTime(now())
        .round(new MatchRound("LaLiga round 11", now()))
        .build();

    mockNotFoundById(nonExistingId);

    webClient
        .mutateWith(csrf())
        .put()
        .uri("/api/v2/matches/" + nonExistingId)
        .body(Mono.just(matchRequest), MatchRequest.class)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void shouldNotUpdateMatchBecauseInvalid() {
    var id = 1L;
    var invalidMatchRequest = MatchRequest.builder().build();

    mockFindById(id);

    webClient
        .mutateWith(csrf())
        .put()
        .uri("/api/v2/matches/" + id)
        .body(Mono.just(invalidMatchRequest), MatchRequest.class)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void shouldUpdateMatchResult() {
    var id = 1L;
    var matchResult = new MatchResult(2, 1);

    mockFindById(id);
    mockSavingMatchEventByProducer();

    webClient
        .mutateWith(csrf())
        .patch()
        .uri("/api/v2/matches/" + id)
        .body(Mono.just(matchResult), MatchResult.class)
        .exchange()
        .expectStatus().isOk();
  }

  @Test
  void shouldNotUpdateMatchResultBecauseNotFound() {
    var nonExistingId = 12324L;
    var matchResult = new MatchResult(2, 1);

    mockNotFoundById(nonExistingId);

    webClient
        .mutateWith(csrf())
        .patch()
        .uri("/api/v2/matches/" + nonExistingId)
        .body(Mono.just(matchResult), MatchResult.class)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void shouldNotUpdateMatchResultBecauseIsInvalid() {
    var id = 1L;
    var invalidMatchResult = new MatchResult(-1, 1);

    mockFindById(id);

    webClient
        .mutateWith(csrf())
        .patch()
        .uri("/api/v2/matches/" + id)
        .body(Mono.just(invalidMatchResult), MatchResult.class)
        .exchange()
        .expectStatus().isBadRequest();
  }
}