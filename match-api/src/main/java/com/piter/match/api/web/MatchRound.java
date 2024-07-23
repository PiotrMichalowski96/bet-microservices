package com.piter.match.api.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

record MatchRound(
    @NotBlank
    @JsonProperty("round_name")
    String roundName,
    @NotNull
    @JsonProperty("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime startTime
) {

    static MatchRound of(com.piter.api.commons.model.MatchRound matchRound) {
        return new MatchRound(
            matchRound.roundName(),
            matchRound.startTime()
        );
    }

  public com.piter.api.commons.model.MatchRound toMatchRound() {
    return new com.piter.api.commons.model.MatchRound(roundName, startTime);
  }
}
