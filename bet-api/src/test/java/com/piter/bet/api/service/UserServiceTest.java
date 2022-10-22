package com.piter.bet.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.piter.api.commons.domain.Bet;
import com.piter.api.commons.domain.BetResult;
import com.piter.api.commons.domain.BetResult.Status;
import com.piter.api.commons.domain.User;
import com.piter.bet.api.config.BetApiTestConfig;
import com.piter.bet.api.model.UserResultProjection;
import com.piter.bet.api.repository.BetRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
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
@Import(BetApiTestConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
class UserServiceTest {

  private static final List<Bet> BETS = List.of(
      Bet.builder()
          .id(1L)
          .user(new User("Jon", "Snow", "snowboard"))
          .betResult(new BetResult(Status.CORRECT, 5))
          .build(),
      Bet.builder()
          .id(2L)
          .user(new User("Tyrion", "Lanister", "bigGuy"))
          .betResult(new BetResult(Status.CORRECT, 1))
          .build(),
      Bet.builder()
          .id(3L)
          .user(new User("Robb", "Stark", "bridegroom"))
          .betResult(new BetResult(Status.INCORRECT, 3))
          .build(),
      Bet.builder()
          .id(4L)
          .user(new User("Tyrion", "Lanister", "bigGuy"))
          .betResult(new BetResult(Status.CORRECT, 1))
          .build(),
      Bet.builder()
          .id(5L)
          .user(new User("Robb", "Stark", "bridegroom"))
          .betResult(new BetResult(Status.CORRECT, 1))
          .build()
  );

  @Autowired
  private BetRepository betRepository;

  @Autowired
  private UserService userService;

  @BeforeEach
  void fillDatabaseIfEmpty() {
    List<Bet> existingBets = betRepository.findAll()
        .collectList()
        .block();
    if (existingBets == null || existingBets.isEmpty()) {
      fillDatabase();
    }
  }

  private void fillDatabase() {
    BETS.forEach(bet -> betRepository.save(bet).block());
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