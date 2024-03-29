package com.piter.api.commons.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Value;

@Value
public class BetResult {
  @NotNull
  Status status;
  @NotNull
  Integer points;

  public enum Status {
    CORRECT, INCORRECT, UNRESOLVED
  }
}
