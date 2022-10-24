package com.piter.bet.api.web;

import static com.piter.bet.api.util.BetTestData.createBetList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOpaqueToken;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockUser;

import com.piter.api.commons.domain.Bet;
import com.piter.api.commons.domain.User;
import com.piter.bet.api.config.BetApiTestConfig;
import com.piter.bet.api.config.SecurityTestConfig;
import com.piter.bet.api.repository.BetRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.reactive.server.WebTestClientConfigurer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = BetController.class)
@Import({BetApiTestConfig.class, SecurityTestConfig.class})
class BetControllerTest {

  private static final String BET_USER = "BET_USER";
  private static final List<Bet> BETS = createBetList();

  @MockBean
  private BetRepository betRepository;

  @Autowired
  private WebTestClient webClient;

  @BeforeEach
  void initMocks() {
    mockFindAll();
  }

  private void mockFindAll() {
    when(betRepository.findAll()).thenReturn(Flux.fromIterable(BETS));
  }

  @Test
  void shouldReturnUnauthorized() {
    webClient
        .get()
        .uri("/bets")
        .exchange()
        .expectStatus().isUnauthorized();
  }

  @Test
  @WithMockUser
  void shouldReturnForbidden() {
    webClient
        .mutateWith(mockUser())
        .get()
        .uri("/bets")
        .exchange()
        .expectStatus().isForbidden();
  }

  @Test
  @WithMockUser
  void shouldGetAllVisibleBets() {
    var user = BETS.get(0).getUser();
    webClient
        .mutateWith(mockBearerToken(user))
        .get()
        .uri("/bets")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Bet.class)
        .value(bets -> assertThat(bets).hasSize(2));
  }

  @Test
  @WithMockUser
  void shouldGetAllVisibleBetsByMatchId() {
    var user = BETS.get(0).getUser();
    var matchId = 1L;
    var expectedBetId = "1";
    mockFindAllByMatchId(matchId);
    webClient
        .mutateWith(mockBearerToken(user))
        .get()
        .uri("/bets?matchId=" + matchId)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Bet.class)
        .value(bets -> {
          assertThat(bets).hasSize(1);
          assertThat(bets.get(0).getId()).isEqualTo(expectedBetId);
        });
  }

  private void mockFindAllByMatchId(Long matchId) {
    Flux<Bet> mockedBetFlux = Flux.fromIterable(BETS)
        .filter(bet -> Objects.equals(bet.getMatch().getId(), matchId));
    when(betRepository.findAllByMatchId(eq(matchId))).thenReturn(mockedBetFlux);
  }

  @Test
  @WithMockUser
  void shouldGetAllVisibleBetsByUserNickname() {
    var user = BETS.get(0).getUser();
    var nickname = BETS.get(2).getUser().getNickname();
    mockFindAllByUserNickName(nickname);
    var expectedBetId = "3";
    webClient
        .mutateWith(mockBearerToken(user))
        .get()
        .uri("/bets?userNickname=" + nickname)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Bet.class)
        .value(bets -> {
          assertThat(bets).hasSize(1);
          assertThat(bets.get(0).getId()).isEqualTo(expectedBetId);
        });
  }

  private void mockFindAllByUserNickName(String userNickname) {
    Flux<Bet> mockedBetFlux = Flux.fromIterable(BETS)
        .filter(bet -> Objects.equals(bet.getUser().getNickname(), userNickname));
    when(betRepository.findAllByUserNickname(eq(userNickname))).thenReturn(mockedBetFlux);
  }

  private WebTestClientConfigurer mockBearerToken(User user) {
    Consumer<Map<String, Object>> attributesConsumer = attributes -> {
      attributes.put("name", createFullNameFrom(user));
      attributes.put("username", user.getNickname());
    };
    return mockOpaqueToken()
        .authorities(new SimpleGrantedAuthority(BET_USER))
        .attributes(attributesConsumer);
  }

  private String createFullNameFrom(User user) {
    return user.getFirstName() + StringUtils.SPACE + user.getLastName();
  }

  @Test
  @WithMockUser
  void shouldGetBetById() {
    var id = "2";
    var userOwningBet = BETS.get(1).getUser();
    mockFindById(id);
    webClient
        .mutateWith(mockBearerToken(userOwningBet))
        .get()
        .uri("/bets/" + id)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Bet.class)
        .value(bet -> assertThat(bet.getId()).isEqualTo(id));
  }

  @Test
  @WithMockUser
  void shouldNotGetBetByIdBecauseUserIsNotBetOwner() {
    var id = "2";
    var userNotOwningBet = BETS.get(0).getUser();
    mockFindById(id);
    webClient
        .mutateWith(mockBearerToken(userNotOwningBet))
        .get()
        .uri("/bets/" + id)
        .exchange()
        .expectStatus().isNotFound();
  }

  private void mockFindById(String id) {
    Bet bet = BETS.stream()
        .filter(b -> Objects.equals(id, b.getId()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Bet with this id does not exist"));
    when(betRepository.findById(id)).thenReturn(Mono.just(bet));
  }
}