package com.piter.bet.event.aggregator.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
public record Match(
    @NotNull
    Long id,
    @NotBlank
    String homeTeam,
    @NotBlank
    String awayTeam,
    @NotNull
    LocalDateTime startTime,
    @Valid
    MatchResult result,
    @NotNull
    @Valid
    MatchRound round
) {

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Match match = (Match) o;
        return Objects.equals(id, match.id) && Objects.equals(homeTeam,
            match.homeTeam) && Objects.equals(awayTeam, match.awayTeam)
            && Objects.equals(round, match.round) && Objects.equals(startTime,
            match.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, homeTeam, awayTeam, startTime, round);
    }
}
