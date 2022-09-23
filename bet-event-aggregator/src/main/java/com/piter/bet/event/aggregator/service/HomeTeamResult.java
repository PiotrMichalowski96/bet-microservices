package com.piter.bet.event.aggregator.service;

import java.util.stream.Stream;

public enum HomeTeamResult {

  WIN() {
    @Override
    protected boolean isResult(int homeTeamGoals, int awayTeamGoals) {
      return homeTeamGoals > awayTeamGoals;
    }
  },
  DRAW {
    @Override
    protected boolean isResult(int homeTeamGoals, int awayTeamGoals) {
      return homeTeamGoals == awayTeamGoals;
    }
  },
  LOSE {
    @Override
    protected boolean isResult(int homeTeamGoals, int awayTeamGoals) {
      return homeTeamGoals < awayTeamGoals;
    }
  };

  protected abstract boolean isResult(int homeTeamGoals, int awayTeamGoals);

  public static HomeTeamResult getResult(Integer homeTeamGoals, Integer awayTeamGoals) {
    return Stream.of(HomeTeamResult.values())
        .filter(result -> result.isResult(homeTeamGoals, awayTeamGoals))
        .findFirst()
        .orElseThrow(() -> new RuntimeException("This case is not supported")); //TODO: generic
  }
}
