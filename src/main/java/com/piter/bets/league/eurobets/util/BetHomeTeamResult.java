package com.piter.bets.league.eurobets.util;

import com.piter.bets.league.eurobets.entity.common.HomeTeamResult;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BetHomeTeamResult {

  WIN(HomeTeamResult.WIN),
  DRAW(HomeTeamResult.DRAW),
  LOSE(HomeTeamResult.LOSE);

  private final HomeTeamResult homeTeamResult;

  public static HomeTeamResult getResult(Integer homeTeamGoals, Integer awayTeamGoals) {
    if (homeTeamGoals > awayTeamGoals)
      return WIN.getHomeTeamResult();
    else if (homeTeamGoals < awayTeamGoals)
      return LOSE.getHomeTeamResult();
    else
      return DRAW.getHomeTeamResult();
  }
}
