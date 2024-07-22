package com.piter.api.commons.model;

import java.time.LocalDateTime;

public record MatchRound(
    String roundName,
    LocalDateTime startTime) {

}
