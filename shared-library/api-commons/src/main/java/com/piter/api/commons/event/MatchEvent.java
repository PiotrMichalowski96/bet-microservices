package com.piter.api.commons.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record MatchEvent(
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
    MatchScore result,
    @JsonProperty("tournament_round")
    TournamentRound tournamentRound
) {

}
