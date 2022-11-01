package com.piter.bet.event.aggregator.domain;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class MatchResult {
  @NotNull
  @Min(value = 0L, message = "Goals must be positive number")
  Integer homeTeamGoals;
  @NotNull
  @Min(value = 0L, message = "Goals must be positive number")
  Integer awayTeamGoals;
}
