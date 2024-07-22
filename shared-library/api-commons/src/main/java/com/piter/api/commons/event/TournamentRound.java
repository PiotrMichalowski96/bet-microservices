package com.piter.api.commons.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record TournamentRound(
    @JsonProperty("name")
    String name,
    @JsonProperty("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime startTime
) {

}
