package com.piter.match.api.domain;

import java.time.LocalDateTime;
import lombok.Value;

@Value
public class MatchRound {
  String roundName;
  LocalDateTime startTime;
}
