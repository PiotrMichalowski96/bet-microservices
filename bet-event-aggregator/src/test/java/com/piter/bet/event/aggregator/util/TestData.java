package com.piter.bet.event.aggregator.util;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.BetResult;
import com.piter.bet.event.aggregator.domain.BetResult.Status;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.domain.MatchResult;
import com.piter.bet.event.aggregator.domain.MatchRound;
import com.piter.bet.event.aggregator.domain.User;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestData {

  public static Match createMatchWithTime(LocalDateTime startTime) {
    return Match.builder()
        .id(1L)
        .homeTeam("FC Barcelona")
        .awayTeam("Real Madrid")
        .startTime(startTime)
        .round(MatchRound.builder()
            .roundName("LaLiga round 30")
            .startTime(LocalDateTime.MAX)
            .build())
        .build();
  }

  public static Match createMatchWithoutResult() {
    return createMatchWithTime(LocalDateTime.MAX);
  }

  public static Match createMatchWithResult() {
    return Match.builder()
        .id(1L)
        .homeTeam("FC Barcelona")
        .awayTeam("Real Madrid")
        .startTime(LocalDateTime.MAX)
        .round(MatchRound.builder()
            .roundName("LaLiga round 30")
            .startTime(LocalDateTime.MAX)
            .build())
        .result(MatchResult.builder()
            .homeTeamGoals(2)
            .awayTeamGoals(1)
            .build())
        .build();
  }

  public static Match createSecondMatchWithoutResult() {
    return Match.builder()
        .id(2L)
        .homeTeam("Sevilla")
        .awayTeam("Athletic Bilbao")
        .startTime(LocalDateTime.MAX)
        .round(MatchRound.builder()
            .roundName("LaLiga round 30")
            .startTime(LocalDateTime.MAX)
            .build())
        .build();
  }

  public static Match createMatchThatAlreadyPassed() {
    return Match.builder()
        .id(1L)
        .homeTeam("FC Barcelona")
        .awayTeam("Real Madrid")
        .startTime(LocalDateTime.MIN)
        .round(MatchRound.builder()
            .roundName("LaLiga round 30")
            .startTime(LocalDateTime.MIN)
            .build())
        .result(MatchResult.builder()
            .awayTeamGoals(2)
            .homeTeamGoals(1)
            .build())
        .build();
  }

  public static Bet createBetRequestWithCorrectPrediction() {
    return Bet.builder()
        .id("1")
        .matchPredictedResult(MatchResult.builder()
            .homeTeamGoals(2)
            .awayTeamGoals(1)
            .build())
        .match(createMatchWithoutResult())
        .user(createUser())
        .betResult(createEmptyBetResult())
        .build();
  }

  public static Bet createBetRequestWithWrongPrediction() {
    return Bet.builder()
        .id("1")
        .matchPredictedResult(MatchResult.builder()
            .homeTeamGoals(1)
            .awayTeamGoals(3)
            .build())
        .match(createMatchWithoutResult())
        .user(createUser())
        .betResult(createEmptyBetResult())
        .build();
  }

  public static Bet createSecondBetRequest() {
    return Bet.builder()
        .id("2")
        .matchPredictedResult(MatchResult.builder()
            .homeTeamGoals(3)
            .awayTeamGoals(2)
            .build())
        .match(createSecondMatchWithoutResult())
        .user(createUser())
        .betResult(createEmptyBetResult())
        .build();
  }

  public static Bet createBetWithoutResult() {
    return createBetRequestWithCorrectPrediction();
  }

  public static Bet createSecondBetWithoutResult() {
    return createSecondBetRequest();
  }

  public static Bet createBetWithCorrectResult() {
    return Bet.builder()
        .id("1")
        .matchPredictedResult(MatchResult.builder()
            .homeTeamGoals(2)
            .awayTeamGoals(1)
            .build())
        .match(createMatchWithResult())
        .user(createUser())
        .betResult(createCorrectBetResult())
        .build();
  }

  public static Bet createBetWithIncorrectResult() {
    return Bet.builder()
        .id("1")
        .matchPredictedResult(MatchResult.builder()
            .homeTeamGoals(1)
            .awayTeamGoals(3)
            .build())
        .match(createMatchWithResult())
        .user(createUser())
        .betResult(createWrongBetResult())
        .build();
  }

  public static User createUser() {
    return User.builder()
        .firstName("Bob")
        .lastName("Marley")
        .nickname("Bobi")
        .build();
  }

  public static BetResult createEmptyBetResult() {
    return BetResult.builder()
        .status(Status.UNRESOLVED)
        .points(0)
        .build();
  }

  private static BetResult createCorrectBetResult() {
    return BetResult.builder()
        .status(Status.CORRECT)
        .points(5)
        .build();
  }

  private static BetResult createWrongBetResult() {
    return BetResult.builder()
        .status(Status.INCORRECT)
        .points(0)
        .build();
  }
}
