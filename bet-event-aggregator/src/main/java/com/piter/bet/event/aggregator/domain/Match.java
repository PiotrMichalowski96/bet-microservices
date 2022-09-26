package com.piter.bet.event.aggregator.domain;

import java.time.LocalDateTime;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
