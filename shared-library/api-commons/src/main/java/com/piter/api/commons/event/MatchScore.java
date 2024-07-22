package com.piter.api.commons.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public record MatchScore(
    @JsonProperty("home_team_score")
    Integer homeTeamScore,
    @JsonProperty("away_team_score")
    Integer awayTeamScore
) {

}
