package com.piter.bet.event.aggregator.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
//  @JsonDeserialize(using = LocalDateTimeDeserializer.class)
//  @JsonSerialize(using = LocalDateTimeSerializer.class)
  LocalDateTime startTime;
  MatchResult result;
  MatchRound round;
}
