package com.piter.bet.event.aggregator.service;

import com.piter.bet.event.aggregator.domain.MatchResult;
import java.util.stream.Stream;
import lombok.NonNull;

public enum HomeTeamResult {

  WIN() {
    @Override
    protected boolean isResult(@NonNull MatchResult matchResult) {
      return matchResult.getHomeTeamGoals() > matchResult.getAwayTeamGoals();
    }
  },
  DRAW {
    @Override
    protected boolean isResult(@NonNull MatchResult matchResult) {
      return matchResult.getHomeTeamGoals().equals(matchResult.getAwayTeamGoals());
    }
  },
  LOSE {
    @Override
    protected boolean isResult(@NonNull MatchResult matchResult) {
      return matchResult.getHomeTeamGoals() < matchResult.getAwayTeamGoals();
    }
  };

  protected abstract boolean isResult(@NonNull MatchResult matchResult);

  public static HomeTeamResult getResult(@NonNull MatchResult matchResult) {
    return Stream.of(HomeTeamResult.values())
        .filter(result -> result.isResult(matchResult))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("This case is not supported")); //TODO: generic
  }
}
