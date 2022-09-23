package com.piter.bet.event.aggregator.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class Bet {
  @NotNull
  Long id;
  @NotNull
  Integer homeTeamGoalBet;
  @NotNull
  Integer awayTeamGoalBet;
  @NotNull
  @Valid
  Match match;
  @NotNull
  @Valid
  User user;
  @NotNull
  @Valid
  BetResults betResults;
}
