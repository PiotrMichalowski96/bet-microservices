package com.piter.bet.event.aggregator.util;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.BetResults;
import com.piter.bet.event.aggregator.domain.BetResults.Status;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.domain.MatchResult;
import com.piter.bet.event.aggregator.domain.MatchRound;
import com.piter.bet.event.aggregator.domain.User;
import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestData {

  public Match createMatchWithoutResult() {
    return Match.builder()
        .id(1L)
        .homeTeam("FC Barcelona")
        .awayTeam("Real Madrid")
        .startTime(LocalDateTime.of(2022, 2, 17, 21, 0, 0))
        .round(MatchRound.builder()
            .roundName("LaLiga round 30")
            .startTime(LocalDateTime.of(2022, 2, 14, 21, 0, 0))
            .build())
        .build();
  }

  public Match createMatchWithResult() {
    return Match.builder()
        .id(1L)
        .homeTeam("FC Barcelona")
        .awayTeam("Real Madrid")
        .startTime(LocalDateTime.of(2022, 2, 17, 21, 0, 0))
        .round(MatchRound.builder()
            .roundName("LaLiga round 30")
            .startTime(LocalDateTime.of(2022, 2, 14, 21, 0, 0))
            .build())
        .result(MatchResult.builder()
            .awayTeamGoals(1)
            .homeTeamGoals(1)
            .build())
        .build();
  }

  public Match createSecondMatchWithoutResult() {
    return Match.builder()
        .id(2L)
        .homeTeam("Sevilla")
        .awayTeam("Athletic Bilbao")
        .startTime(LocalDateTime.of(2022, 2, 18, 21, 0, 0))
        .round(MatchRound.builder()
            .roundName("LaLiga round 30")
            .startTime(LocalDateTime.of(2022, 2, 14, 21, 0, 0))
            .build())
        .build();
  }

  public Bet createBetRequestWithCorrectPrediction() {
    return Bet.builder()
        .id(1L)
        .homeTeamGoalBet(1)
        .awayTeamGoalBet(1)
        .match(createMatchWithoutResult())
        .user(createUser())
        .betResults(createEmptyBetResult())
        .build();
  }

  public Bet createBetRequestWithWrongPrediction() {
    return Bet.builder()
        .id(1L)
        .homeTeamGoalBet(2)
        .awayTeamGoalBet(0)
        .match(createMatchWithoutResult())
        .user(createUser())
        .betResults(createEmptyBetResult())
        .build();
  }

  public Bet createSecondBetRequest() {
    return Bet.builder()
        .id(2L)
        .homeTeamGoalBet(3)
        .awayTeamGoalBet(2)
        .match(createSecondMatchWithoutResult())
        .user(createUser())
        .betResults(createEmptyBetResult())
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
        .homeTeamGoalBet(1)
        .awayTeamGoalBet(1)
        .match(createMatchWithResult())
        .user(createUser())
        .betResults(createCorrectBetResult())
        .build();
  }

  public Bet createBetWithIncorrectResult() {
    return Bet.builder()
        .id(1L)
        .homeTeamGoalBet(2)
        .awayTeamGoalBet(0)
        .match(createMatchWithResult())
        .user(createUser())
        .betResults(createWrongBetResult())
        .build();
  }

  private User createUser() {
    return User.builder()
        .firstName("Bob")
        .lastName("Marley")
        .nickname("Bobi")
        .build();
  }

  private BetResults createEmptyBetResult() {
    return BetResults.builder()
        .status(Status.UNRESOLVED)
        .points(0L)
        .build();
  }

  private BetResults createCorrectBetResult() {
    return BetResults.builder()
        .status(Status.CORRECT)
        .points(3L)
        .build();
  }

  private BetResults createWrongBetResult() {
    return BetResults.builder()
        .status(Status.INCORRECT)
        .points(3L)
        .build();
  }
}