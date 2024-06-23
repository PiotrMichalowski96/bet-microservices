package com.piter.api.commons.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Builder
@Document
public record Bet(
    @Id
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
    BetResult betResult
) {

}
