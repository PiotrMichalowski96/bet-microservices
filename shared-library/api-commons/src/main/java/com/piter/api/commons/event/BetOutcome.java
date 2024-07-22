package com.piter.api.commons.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BetOutcome(
    @JsonProperty("state")
    State state,
    @JsonProperty("points")
    Integer points
) {

  public enum State {
    CORRECT, INCORRECT, ONGOING
  }
}
