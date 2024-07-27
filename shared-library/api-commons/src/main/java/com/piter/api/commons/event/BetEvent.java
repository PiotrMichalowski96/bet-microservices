package com.piter.api.commons.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BetEvent(
    @JsonProperty("id")
    String id,
    @JsonProperty("predicted_match_score")
    MatchScore predictedMatchScore,
    @JsonProperty("match")
    MatchEvent match,
    @JsonProperty("bet_user")
    BetUser betUser,
    @JsonProperty("bet_outcome")
    BetOutcome betOutcome
) {

}
