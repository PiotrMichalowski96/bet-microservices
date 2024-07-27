package com.piter.api.commons.model;

import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Document
public record Match(
    @Id
    Long id,
    String homeTeam,
    String awayTeam,
    LocalDateTime startTime,
    MatchResult result,
    MatchRound round
) {

  @Transient
  public static final String SEQUENCE_NAME = "match_sequence";
}
