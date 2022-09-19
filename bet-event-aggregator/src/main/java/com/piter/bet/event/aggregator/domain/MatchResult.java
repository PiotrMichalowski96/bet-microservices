package com.piter.bet.event.aggregator.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class MatchResult {
  Integer homeTeamGoals;
  Integer awayTeamGoals;
}
