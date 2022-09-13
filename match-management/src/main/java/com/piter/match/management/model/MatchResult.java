package com.piter.match.management.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@AllArgsConstructor
@Builder
public class MatchResult {
  Integer homeTeamGoals;
  Integer awayTeamGoals;
}
