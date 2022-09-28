package com.piter.bet.api.domain;

import javax.validation.constraints.NotNull;
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
