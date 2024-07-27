package com.piter.api.commons.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.piter.api.commons.model.Match;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Builder;

@Builder
public record MatchEvent(
    @JsonProperty("id")
    Long id,
    @JsonProperty("home_team")
    String homeTeam,
    @JsonProperty("away_team")
    String awayTeam,
    @JsonProperty("start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime startTime,
    @JsonProperty("result")
    MatchScore result,
    @JsonProperty("tournament_round")
    TournamentRound tournamentRound
) {

    public Match toMatch() {
        return Match.builder()
            .id(id)
            .homeTeam(homeTeam)
            .awayTeam(awayTeam)
            .startTime(startTime)
            .result(Optional.ofNullable(result)
                .map(MatchScore::toMatchResult)
                .orElse(null))
            .round(Optional.ofNullable(tournamentRound)
                .map(TournamentRound::toMatchRound)
                .orElse(null))
            .build();
    }

    public static MatchEvent of(Match match) {
        return MatchEvent.builder()
            .id(match.id())
            .homeTeam(match.homeTeam())
            .awayTeam(match.awayTeam())
            .startTime(match.startTime())
            .result(MatchScore.of(match.result()))
            .tournamentRound(TournamentRound.of(match.round()))
            .build();
    }
}
