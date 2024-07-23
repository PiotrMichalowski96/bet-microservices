package com.piter.match.api.web;

import com.fasterxml.jackson.annotation.JsonProperty;

record MatchIdResponse(
    @JsonProperty("match_id")
    Long matchId
) {

}
