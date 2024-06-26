package com.piter.bet.event.aggregator.prediction.match;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.domain.MatchResult;
import java.util.Optional;

enum PredictionType {

  AWAY_TEAM_PREDICTION() {
    @Override
    int getGoals(Bet bet) {
      return Optional.ofNullable(bet)
          .map(Bet::matchPredictedResult)
          .map(MatchResult::awayTeamGoals)
          .orElse(0);
    }
  },
  HOME_TEAM_PREDICTION {
    @Override
    int getGoals(Bet bet) {
      return Optional.ofNullable(bet)
          .map(Bet::matchPredictedResult)
          .map(MatchResult::homeTeamGoals)
          .orElse(0);
    }
  },
  AWAY_TEAM_RESULT {
    @Override
    int getGoals(Bet bet) {
      return Optional.ofNullable(bet)
          .map(Bet::match)
          .map(Match::result)
          .map(MatchResult::awayTeamGoals)
          .orElse(0);
    }
  },
  HOME_TEAM_RESULT {
    @Override
    int getGoals(Bet bet) {
      return Optional.ofNullable(bet)
          .map(Bet::match)
          .map(Match::result)
          .map(MatchResult::homeTeamGoals)
          .orElse(0);
    }
  };

  abstract int getGoals(Bet bet);
}
