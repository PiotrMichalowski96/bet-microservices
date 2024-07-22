package com.piter.api.commons.model;

import jakarta.validation.constraints.NotNull;

public record BetResult(
    @NotNull
    Status status,
    @NotNull
    Integer points) {

  public enum Status {
    CORRECT, INCORRECT, UNRESOLVED
  }
}
