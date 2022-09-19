package com.piter.bet.event.aggregator.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class MatchRound {
  String roundName;
  LocalDateTime startTime;
}
