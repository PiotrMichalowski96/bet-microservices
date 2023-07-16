package com.piter.bet.event.aggregator.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class Match {
  @NotNull
  Long id;
  @NotBlank
  String homeTeam;
  @NotBlank
  String awayTeam;
  @NotNull
  LocalDateTime startTime;
  @Valid
  @EqualsAndHashCode.Exclude
  MatchResult result;
  @NotNull
  @Valid
  MatchRound round;
}
