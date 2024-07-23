package com.piter.match.api.web;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

record MatchResult(
    @NotNull
    @Min(value = 0L, message = "Goals must be positive number")
    @JsonProperty("home_team_goals")
    Integer homeTeamGoals,
    @NotNull
    @Min(value = 0L, message = "Goals must be positive number")
    @JsonProperty("away_team_goals")
    Integer awayTeamGoals
) {

    static MatchResult of(com.piter.api.commons.model.MatchResult matchResult) {
        if (matchResult == null) {
            return null;
        }
        return new MatchResult(
            matchResult.homeTeamGoals(),
            matchResult.awayTeamGoals()
        );
    }

    public com.piter.api.commons.model.MatchResult toMatchResult() {
        return new com.piter.api.commons.model.MatchResult(homeTeamGoals, awayTeamGoals);
    }
}
