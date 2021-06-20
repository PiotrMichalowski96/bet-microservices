package com.piter.bets.league.eurobets.util;

import com.piter.bets.league.eurobets.entity.Bet;
import com.piter.bets.league.eurobets.entity.BetResults;
import com.piter.bets.league.eurobets.entity.MatchResult;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BetRules {

  BET_CORRECT_RESULT(4L,
      (bet, matchResult) -> bet.getAwayTeamGoalBet().equals(matchResult.getAwayTeamGoals()) &&
          bet.getHomeTeamGoalBet().equals(matchResult.getHomeTeamGoals())
  ),
  BET_DRAW(3L,
      (bet, matchResult) -> bet.getHomeTeamGoalBet().equals(bet.getAwayTeamGoalBet()) &&
          matchResult.getHomeTeamGoals().equals(matchResult.getAwayTeamGoals())
  ),
  BET_ONE_TEAM_GOALS(3L,
      (bet, matchResult) -> bet.getAwayTeamGoalBet().equals(matchResult.getAwayTeamGoals()) ||
          bet.getHomeTeamGoalBet().equals(matchResult.getHomeTeamGoals())
  ),
  BET_NO_CORRECT_GOALS(2L,
      (bet, matchResult) -> !bet.getAwayTeamGoalBet().equals(matchResult.getAwayTeamGoals()) &&
          !bet.getHomeTeamGoalBet().equals(matchResult.getHomeTeamGoals())
  );

  private final long scorePoints;
  private final BiPredicate<Bet, MatchResult> betRulesPredicate;

  public static Bet getBetWithResult(Bet bet, MatchResult matchResult) {

    Long calculatedPoints = calculatePointsForBet(bet, matchResult);

    BetResults betResults = BetResults.builder()
        .points(calculatedPoints)
        .bet(bet)
        .build();

    bet.setBetResults(betResults);
    return bet;
  }

  private static Long calculatePointsForBet(Bet bet, MatchResult matchResult) {
    HomeTeamResult homeTeamResultFromBet = HomeTeamResult.getResult(bet.getHomeTeamGoalBet(),
        bet.getAwayTeamGoalBet());

    HomeTeamResult correctHomeTeamResult = HomeTeamResult.getResult(
        matchResult.getHomeTeamGoals(), matchResult.getAwayTeamGoals());

    // betting correct match result (win, draw, lose) is necessary condition to score points
    if (correctHomeTeamResult.equals(homeTeamResultFromBet)) {
      return Stream.of(BetRules.values())
          .filter(betRule -> betRule.getBetRulesPredicate().test(bet, matchResult))
          .map(BetRules::getScorePoints)
          .findFirst()
          .orElse(0L);
    } else {
      return 0L;
    }
  }
}
