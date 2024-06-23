package com.piter.api.commons.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotBlank
    String homeTeam,
    @NotBlank
    String awayTeam,
    @NotNull
    LocalDateTime startTime,
    @Valid
    MatchResult result,
    @NotNull
    @Valid
    MatchRound round
) {

  @Transient
  public static final String SEQUENCE_NAME = "match_sequence";
}
