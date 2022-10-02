package com.piter.bet.api.domain;

import java.time.LocalDateTime;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Value;

@Value
public class MatchRound {
  @NotBlank
  String roundName;
  @NotNull
  LocalDateTime startTime;
}
