package com.piter.bet.event.aggregator.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
public record MatchResult(
    @NotNull
    @Min(value = 0L, message = "Goals must be positive number")
    Integer homeTeamGoals,
    @NotNull
    @Min(value = 0L, message = "Goals must be positive number")
    Integer awayTeamGoals
) {

}
