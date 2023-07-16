package com.piter.api.commons.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Value;

@Value
public class MatchRound {
  @NotBlank
  String roundName;
  @NotNull
  LocalDateTime startTime;
}
