package com.piter.match.api.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@Builder
@Document
public class Match {

  @Transient
  public static final String SEQUENCE_NAME = "match_sequence";

  @Id
  Long id;
  String homeTeam;
  String awayTeam;
  LocalDateTime startTime;
  MatchResult result;
  MatchRound round;
}
