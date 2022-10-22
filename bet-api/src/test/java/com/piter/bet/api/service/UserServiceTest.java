package com.piter.bet.api.service;

import static com.piter.bet.api.util.BetTestData.createBetListWithBetResults;
import static org.assertj.core.api.Assertions.assertThat;

import com.piter.bet.api.model.UserResultProjection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

class UserServiceTest extends AbstractServiceTest {

  private final UserService userService;

  @Autowired
  public UserServiceTest(UserService userService) {
    super(createBetListWithBetResults());
    this.userService = userService;
  }

  @Test
  void shouldGetUsersWithPoints() {
    Flux<UserResultProjection> userResultFlux = userService.findAllUserResults();
    StepVerifier.create(userResultFlux)
        .assertNext(userResult -> assertNicknameAndPoints(userResult, "snowboard", 5L))
        .assertNext(userResult -> assertNicknameAndPoints(userResult, "bridegroom", 4L))
        .assertNext(userResult -> assertNicknameAndPoints(userResult, "bigGuy", 2L))
        .verifyComplete();
  }

  @ParameterizedTest
  @CsvSource({"bridegroom,4", "snowboard, 5", "bigGuy,2"})
  void shouldGetParticularUserWithPoints(String nickname, Long points) {
    Mono<UserResultProjection> userResultMono = userService.findUserResultByNickname(nickname);
    StepVerifier.create(userResultMono)
        .assertNext(userResult -> assertNicknameAndPoints(userResult, nickname, points))
        .verifyComplete();
  }

  private void assertNicknameAndPoints(UserResultProjection userResult, String nickname, Long points) {
    assertThat(userResult.getUser().getNickname()).isEqualTo(nickname);
    assertThat(userResult.getPoints()).isEqualTo(points);
  }
}