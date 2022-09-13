package com.piter.match.management.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@AllArgsConstructor
@Builder
public class MatchRound {
  String roundName;
  LocalDateTime startTime;
}
