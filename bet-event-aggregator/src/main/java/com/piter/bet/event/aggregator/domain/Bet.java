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
  @Valid
  MatchResult matchPredictedResult;
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
