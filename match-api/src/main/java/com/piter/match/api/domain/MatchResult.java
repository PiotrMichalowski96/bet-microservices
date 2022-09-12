package com.piter.match.api.domain;

import javax.validation.constraints.NotNull;
import lombok.Value;

@Value
public class MatchResult {
  @NotNull
  Integer homeTeamGoals;
  @NotNull
  Integer awayTeamGoals;
}
