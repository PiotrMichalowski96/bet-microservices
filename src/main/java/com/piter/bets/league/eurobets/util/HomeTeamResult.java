package com.piter.bets.league.eurobets.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum HomeTeamResult {

  WIN,
  DRAW,
  LOSE;

  public static HomeTeamResult getResult(Integer homeTeamGoals, Integer awayTeamGoals) {
    if (homeTeamGoals > awayTeamGoals)
      return WIN;
    else if (homeTeamGoals < awayTeamGoals)
      return LOSE;
    else
      return DRAW;
  }
}
