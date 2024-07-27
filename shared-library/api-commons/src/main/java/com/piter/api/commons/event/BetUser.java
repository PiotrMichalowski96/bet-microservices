package com.piter.api.commons.event;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BetUser(
    @JsonProperty("first_name")
    String firstName,
    @JsonProperty("last_name")
    String lastName,
    @JsonProperty("nickname")
    String nickname
) {

}
