package com.piter.match.api.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.piter.api.commons.model.Match;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Builder;

@Builder
record MatchRequest(
    @NotBlank
    @JsonProperty("home_team")
    String homeTeam,
    @NotBlank
    @JsonProperty("away_team")
    String awayTeam,
    @NotNull
    @JsonProperty("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime startTime,
    @Valid
    @JsonProperty("result")
    MatchResult result,
    @Valid
    @JsonProperty("round")
    MatchRound round
) {

    public Match toMatch() {
        return Match.builder()
            .homeTeam(homeTeam)
            .awayTeam(awayTeam)
            .startTime(startTime)
            .result(Optional.ofNullable(result)
                .map(MatchResult::toMatchResult)
                .orElse(null))
            .round(Optional.ofNullable(round)
                .map(MatchRound::toMatchRound)
                .orElse(null))
            .build();
    }
}
