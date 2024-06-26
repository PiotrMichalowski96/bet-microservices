package com.piter.bet.event.aggregator.service;

import static com.piter.bet.event.aggregator.exception.BetAggregatorException.cannotRetrieveResultException;

import com.piter.bet.event.aggregator.domain.MatchResult;
import java.util.stream.Stream;
import lombok.NonNull;

enum HomeTeamResult {

  WIN() {
    @Override
    protected boolean isResult(@NonNull MatchResult matchResult) {
      return matchResult.homeTeamGoals() > matchResult.awayTeamGoals();
    }
  },
  DRAW {
    @Override
    protected boolean isResult(@NonNull MatchResult matchResult) {
      return matchResult.homeTeamGoals().equals(matchResult.awayTeamGoals());
    }
  },
  LOSE {
    @Override
    protected boolean isResult(@NonNull MatchResult matchResult) {
      return matchResult.homeTeamGoals() < matchResult.awayTeamGoals();
    }
  };

  protected abstract boolean isResult(@NonNull MatchResult matchResult);

  static HomeTeamResult getResult(@NonNull MatchResult matchResult) {
    return Stream.of(HomeTeamResult.values())
        .filter(result -> result.isResult(matchResult))
        .findFirst()
        .orElseThrow(() -> cannotRetrieveResultException(matchResult));
  }
}
