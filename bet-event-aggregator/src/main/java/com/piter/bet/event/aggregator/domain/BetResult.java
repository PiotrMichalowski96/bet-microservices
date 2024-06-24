package com.piter.bet.event.aggregator.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
public record BetResult(
    @NotNull
    Status status,
    @NotNull
    Integer points) {

  public enum Status {
    CORRECT, INCORRECT, UNRESOLVED
  }
}
