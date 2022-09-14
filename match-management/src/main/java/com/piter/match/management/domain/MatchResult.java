package com.piter.match.management.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchResult {
  private Integer homeTeamGoals;
  private Integer awayTeamGoals;
}
