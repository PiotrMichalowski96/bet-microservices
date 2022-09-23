package com.piter.bet.event.aggregator.domain;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
