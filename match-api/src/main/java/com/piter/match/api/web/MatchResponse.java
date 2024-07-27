package com.piter.match.api.web;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.piter.api.commons.model.Match;
import java.time.LocalDateTime;

record MatchResponse(
    @JsonProperty("id")
    Long id,
    @JsonProperty("home_team")
    String homeTeam,
    @JsonProperty("away_team")
    String awayTeam,
    @JsonProperty("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime startTime,
    @JsonProperty("result")
    MatchResult result,
    @JsonProperty("round")
    MatchRound round
) {

    static MatchResponse of(Match match) {
        return new MatchResponse(
            match.id(),
            match.homeTeam(),
            match.awayTeam(),
            match.startTime(),
            MatchResult.of(match.result()),
            MatchRound.of(match.round())
        );
    }
}
