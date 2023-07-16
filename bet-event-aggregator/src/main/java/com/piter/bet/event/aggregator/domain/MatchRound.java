package com.piter.bet.event.aggregator.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class MatchRound {
  @NotBlank
  String roundName;
  @NotNull
  LocalDateTime startTime;
}
