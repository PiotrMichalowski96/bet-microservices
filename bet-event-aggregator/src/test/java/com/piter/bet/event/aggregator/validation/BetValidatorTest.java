package com.piter.bet.event.aggregator.validation;

import static com.piter.bet.event.aggregator.util.TestData.*;
import static com.piter.bet.event.aggregator.util.WaitUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.BetResults;
import com.piter.bet.event.aggregator.domain.BetResults.Status;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.domain.User;
import java.time.LocalDateTime;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = LocalValidatorFactoryBean.class)
class BetValidatorTest {

  @Autowired
  private Validator validator;

  private BetValidator betValidator;

  @BeforeEach
  void init() {
    betValidator = new BetValidator(validator);
  }

  @ParameterizedTest
  @MethodSource("provideInvalidBet")
  void invalidBet(Bet invalidBetRequest) {
    //given input
    //when
    boolean isValid = betValidator.validate(invalidBetRequest);

    //then
    assertThat(isValid).isFalse();
  }

  private static Stream<Arguments> provideInvalidBet() {
    Match correctMatch = createMatchWithoutResult();
    Match invalidMatch = Match.builder().build();
    User user = createUser();
    BetResults betResult = createEmptyBetResult();
    BetResults invalidBetResult = BetResults.builder()
        .status(Status.CORRECT)
        .points(5)
        .build();

    return Stream.of(
        // Missing home team goals prediction
        Arguments.of(Bet.builder()
            .id(1L)
            .awayTeamGoalBet(1)
            .match(correctMatch)
            .user(user)
            .betResults(betResult)
            .build()),
        // Invalid match
        Arguments.of(Bet.builder()
            .id(1L)
            .homeTeamGoalBet(1)
            .awayTeamGoalBet(1)
            .match(invalidMatch)
            .user(user)
            .betResults(betResult)
            .build()),
        // Wrong bet result - bet was resolved and result has points
        Arguments.of(Bet.builder()
            .id(1L)
            .homeTeamGoalBet(1)
            .awayTeamGoalBet(1)
            .match(correctMatch)
            .user(user)
            .betResults(invalidBetResult)
            .build())
        );
  }

  @Test
  void invalidBetBecauseMatchHasAlreadyStarted() {
    //given
    LocalDateTime matchStartTime = LocalDateTime.now();
    Bet invalidBetRequest = Bet.builder()
        .id(1L)
        .homeTeamGoalBet(1)
        .awayTeamGoalBet(1)
        .match(createMatchWithTime(matchStartTime))
        .user(createUser())
        .betResults(createEmptyBetResult())
        .build();

    //when
    waitMillis(200L);
    boolean isValid = betValidator.validate(invalidBetRequest);

    //then
    assertThat(isValid).isFalse();
  }

  @Test
  void validBetRequest() {
    //given
    Bet validBetRequest = createBetWithoutResult();

    //when
    boolean isValid = betValidator.validate(validBetRequest);

    //then
    assertThat(isValid).isTrue();
  }
}