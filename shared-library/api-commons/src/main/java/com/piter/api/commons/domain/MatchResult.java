package com.piter.api.commons.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class MatchResult {
  @NotNull
  @Min(value = 0L, message = "Goals must be positive number")
  Integer homeTeamGoals;
  @NotNull
  @Min(value = 0L, message = "Goals must be positive number")
  Integer awayTeamGoals;
}
