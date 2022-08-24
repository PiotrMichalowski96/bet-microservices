package com.piter.match.service.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class Match {
  Long id;
  String homeTeam;
  String awayTeam;
  LocalDateTime startTime;
  MatchResult result;
  MatchRound round;
}