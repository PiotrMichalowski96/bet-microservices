package com.piter.api.commons.event;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.piter.api.commons.model.MatchResult;

public record MatchScore(
    @JsonProperty("home_team_score")
    Integer homeTeamScore,
    @JsonProperty("away_team_score")
    Integer awayTeamScore
) {

    public MatchResult toMatchResult() {
        return new MatchResult(homeTeamScore, awayTeamScore);
    }

    public static MatchScore of(MatchResult matchResult) {
        if (matchResult == null) {
            return null;
        }
        return new MatchScore(matchResult.homeTeamGoals(), matchResult.awayTeamGoals());
    }
}
