package com.piter.bet.event.aggregator.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
public record Bet(
    @NotNull
    String id,
    @NotNull
    @Valid
    MatchResult matchPredictedResult,
    @NotNull
    @Valid
    Match match,
    @NotNull
    @Valid
    User user,
    @NotNull
    @Valid
    BetResult betResult) {

}
