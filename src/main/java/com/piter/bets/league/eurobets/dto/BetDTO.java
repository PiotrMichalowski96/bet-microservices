package com.piter.bets.league.eurobets.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class BetDTO {

  private Long id;
  private Long matchId;
  private Long userId;
  private Long homeTeamGoalBet;
  private Long awayTeamGoalBet;
  private Long points;
}
