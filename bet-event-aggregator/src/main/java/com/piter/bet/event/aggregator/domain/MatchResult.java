package com.piter.bet.event.aggregator.domain;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class MatchResult {
  @NotNull
  Integer homeTeamGoals;
  @NotNull
  Integer awayTeamGoals;
}
