package com.piter.bet.event.aggregator.prediction.match;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.domain.MatchResult;
import java.util.Optional;

public enum PredictionType {

  AWAY_TEAM_PREDICTION() {
    @Override
    public int getGoals(Bet bet) {
      return Optional.ofNullable(bet)
          .map(Bet::getAwayTeamGoalBet)
          .orElse(0);
    }
  },
  HOME_TEAM_PREDICTION {
    @Override
    public int getGoals(Bet bet) {
      return Optional.ofNullable(bet)
          .map(Bet::getHomeTeamGoalBet)
          .orElse(0);
    }
  },
  AWAY_TEAM_RESULT {
    @Override
    public int getGoals(Bet bet) {
      return Optional.ofNullable(bet)
          .map(Bet::getMatch)
          .map(Match::getResult)
          .map(MatchResult::getAwayTeamGoals)
          .orElse(0);
    }
  },
  HOME_TEAM_RESULT {
    @Override
    public int getGoals(Bet bet) {
      return Optional.ofNullable(bet)
          .map(Bet::getMatch)
          .map(Match::getResult)
          .map(MatchResult::getHomeTeamGoals)
          .orElse(0);
    }
  };

  public abstract int getGoals(Bet bet);
}
