package com.piter.api.commons.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record MatchRound(
    @NotBlank
    String roundName,
    @NotNull
    LocalDateTime startTime) {

}
