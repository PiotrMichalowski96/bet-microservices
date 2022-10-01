package com.piter.bet.api.web;

import static com.piter.bet.api.util.BetTestData.createBetList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.piter.bet.api.config.BetApiTestConfig;
import com.piter.bet.api.domain.Bet;
import com.piter.bet.api.repository.BetRepository;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = BetRouterConfig.class)
@Import(BetApiTestConfig.class)
class BetRouterTest {

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
  void shouldGetAllBets() {
    webClient.get()
        .uri("/bets")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(Bet.class)
        .value(bets -> assertThat(bets).hasSameElementsAs(BETS));
  }

  @Test
  void shouldGetBetById() {
    var id = 2L;
    mockFindById(id);
    webClient.get()
        .uri("/bets/" + id)
        .exchange()
        .expectStatus().isOk()
        .expectBody(Bet.class)
        .value(bet -> assertThat(bet.getId()).isEqualTo(id));
  }

  private void mockFindById(Long id) {
    Bet bet = BETS.stream()
        .filter(b -> Objects.equals(id, b.getId()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Bet with this id does not exist"));
    when(betRepository.findById(id)).thenReturn(Mono.just(bet));
  }
}