package com.piter.bet.api.web;

import static com.piter.bet.api.util.UserTestData.createFirstUserResult;
import static com.piter.bet.api.util.UserTestData.createUserResultList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.piter.bet.api.config.BetApiTestConfig;
import com.piter.bet.api.config.SecurityTestConfig;
import com.piter.bet.api.model.UserResultProjection;
import com.piter.bet.api.repository.BetRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
@WebFluxTest(controllers = UserController.class, properties = "spring.cloud.config.enabled=false")
@Import({BetApiTestConfig.class, SecurityTestConfig.class})
class UserControllerTest {

  private static final List<UserResultProjection> USER_RESULT_PROJECTION_LIST = createUserResultList();

  @MockBean
  private BetRepository betRepository;

  @Autowired
  private WebTestClient webClient;

  @BeforeEach
  void initMocks() {
    mockFindAllUsersResults();
  }

  private void mockFindAllUsersResults() {
    when(betRepository.findAllUsersResults()).thenReturn(Flux.fromIterable(USER_RESULT_PROJECTION_LIST));
  }

  @Test
  void shouldFindAllUserResults() {
    webClient
        .get()
        .uri("/users-results")
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(UserResultProjection.class)
        .value(userResults -> assertThat(userResults).hasSize(3));
  }

  @Test
  void shouldFindUserResultByNickname() {
    UserResultProjection userResult = createFirstUserResult();
    String nickname = userResult.getUser().getNickname();
    mockFindByNickname(nickname, userResult);
    webClient
        .get()
        .uri("/users-results/" + nickname)
        .exchange()
        .expectStatus().isOk()
        .expectBody(UserResultProjection.class)
        .value(userResultProjection -> assertThat(userResultProjection).isEqualTo(userResult));
  }

  private void mockFindByNickname(String nickname, UserResultProjection userResult) {
    when(betRepository.findUserResultByUsersNickname(nickname)).thenReturn(Mono.just(userResult));
  }

}