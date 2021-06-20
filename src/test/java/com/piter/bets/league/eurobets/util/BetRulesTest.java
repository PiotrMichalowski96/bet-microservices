package com.piter.bets.league.eurobets.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.piter.bets.league.eurobets.entity.Bet;
import com.piter.bets.league.eurobets.entity.BetResults;
import com.piter.bets.league.eurobets.entity.MatchResult;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class BetRulesTest {

  @ParameterizedTest
  @MethodSource("provideGoalsAndExpectedPoints")
  public void shouldCalculateCorrectPoints(int homeTeamGoalBet, int awayTeamGoalBet,
      int homeTeamGoalsMatchResult, int awayTeamGoalsMatchResult, long expectedPoints) {

    //given
    Bet bet = Bet.builder()
        .homeTeamGoalBet(homeTeamGoalBet)
        .awayTeamGoalBet(awayTeamGoalBet)
        .build();

    MatchResult matchResult = MatchResult.builder()
        .homeTeamGoals(homeTeamGoalsMatchResult)
        .awayTeamGoals(awayTeamGoalsMatchResult)
        .build();

    //when
    Bet betWithResult = BetRules.getBetWithResult(bet, matchResult);
    BetResults betResult = betWithResult.getBetResults();

    //then
    assertThat(betResult.getPoints()).isEqualTo(expectedPoints);
  }

  private static Stream<Arguments> provideGoalsAndExpectedPoints() {
    return Stream.of(
        Arguments.of(3, 4, 3, 4, 4L), //bet exact match result - 4 points
        Arguments.of(3, 3, 3, 3, 4L), //bet exact match result - 4 points
        Arguments.of(1, 2, 1, 3, 3L), //bet winner and exact home team goals - 3 points
        Arguments.of(1, 3, 0, 3, 3L), //bet winner and exact away team goals - 3 points
        Arguments.of(0, 0, 2, 2, 3L), //bet draw - 3 points
        Arguments.of(1, 0, 3, 2, 2L), //bet draw - 3 points
        Arguments.of(1, 0, 1, 3, 0L), //bet wrong win / draw / lose - 0 points
        Arguments.of(1, 0, 0, 3, 0L)  //bet wrong win / draw / lose - 0 points
    );
  }}
