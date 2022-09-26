package com.piter.bet.event.aggregator.domain;

import javax.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class BetResult {
  @NotNull
  Status status;
  @NotNull
  Integer points;

  public enum Status {
    CORRECT, INCORRECT, UNRESOLVED
  }
}
