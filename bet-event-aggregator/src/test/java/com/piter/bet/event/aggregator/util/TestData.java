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

  public Match createMatchWithTime(LocalDateTime startTime) {
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

  public Match createMatchWithoutResult() {
    return createMatchWithTime(LocalDateTime.MAX);
  }

  public Match createMatchWithResult() {
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

  public Match createSecondMatchWithoutResult() {
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

  public Match createMatchThatAlreadyPassed() {
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

  public Bet createBetRequestWithCorrectPrediction() {
    return Bet.builder()
        .id(1L)
        .matchPredictedResult(MatchResult.builder()
            .homeTeamGoals(2)
            .awayTeamGoals(1)
            .build())
        .match(createMatchWithoutResult())
        .user(createUser())
        .betResult(createEmptyBetResult())
        .build();
  }

  public Bet createBetRequestWithWrongPrediction() {
    return Bet.builder()
        .id(1L)
        .matchPredictedResult(MatchResult.builder()
            .homeTeamGoals(1)
            .awayTeamGoals(3)
            .build())
        .match(createMatchWithoutResult())
        .user(createUser())
        .betResult(createEmptyBetResult())
        .build();
  }

  public Bet createSecondBetRequest() {
    return Bet.builder()
        .id(2L)
        .matchPredictedResult(MatchResult.builder()
            .homeTeamGoals(3)
            .awayTeamGoals(2)
            .build())
        .match(createSecondMatchWithoutResult())
        .user(createUser())
        .betResult(createEmptyBetResult())
        .build();
  }

  public Bet createBetWithoutResult() {
    return createBetRequestWithCorrectPrediction();
  }

  public Bet createSecondBetWithoutResult() {
    return createSecondBetRequest();
  }

  public Bet createBetWithCorrectResult() {
    return Bet.builder()
        .id(1L)
        .matchPredictedResult(MatchResult.builder()
            .homeTeamGoals(2)
            .awayTeamGoals(1)
            .build())
        .match(createMatchWithResult())
        .user(createUser())
        .betResult(createCorrectBetResult())
        .build();
  }

  public Bet createBetWithIncorrectResult() {
    return Bet.builder()
        .id(1L)
        .matchPredictedResult(MatchResult.builder()
            .homeTeamGoals(1)
            .awayTeamGoals(3)
            .build())
        .match(createMatchWithResult())
        .user(createUser())
        .betResult(createWrongBetResult())
        .build();
  }

  public User createUser() {
    return User.builder()
        .firstName("Bob")
        .lastName("Marley")
        .nickname("Bobi")
        .build();
  }

  public BetResult createEmptyBetResult() {
    return BetResult.builder()
        .status(Status.UNRESOLVED)
        .points(0)
        .build();
  }

  private BetResult createCorrectBetResult() {
    return BetResult.builder()
        .status(Status.CORRECT)
        .points(5)
        .build();
  }

  private BetResult createWrongBetResult() {
    return BetResult.builder()
        .status(Status.INCORRECT)
        .points(0)
        .build();
  }
}
