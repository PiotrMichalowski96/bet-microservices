package com.piter.match.api.util;

import com.piter.api.commons.domain.Match;
import com.piter.api.commons.domain.MatchResult;
import com.piter.api.commons.domain.MatchRound;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MatchTestData {

  public static List<Match> createMatchList() {
    return List.of(
        Match.builder()
            .id(1L)
            .homeTeam("FC Barcelona")
            .awayTeam("Real Madrid")
            .startTime(LocalDateTime.now().plus(2, ChronoUnit.DAYS))
            .round(new MatchRound("LaLiga round 30", LocalDateTime.of(2022, 2, 14, 21, 0, 0)))
            .build(),
        Match.builder()
            .id(2L)
            .homeTeam("Sevilla")
            .awayTeam("Athletic Bilbao")
            .startTime(LocalDateTime.now().plus(1, ChronoUnit.DAYS))
            .round(new MatchRound("LaLiga round 29", LocalDateTime.of(2022, 2, 7, 21, 0, 0)))
            .build(),
        Match.builder()
            .id(3L)
            .homeTeam("Atletico Madrid")
            .awayTeam("Valencia")
            .startTime(LocalDateTime.of(2022, 2, 15, 21, 0, 0))
            .result(new MatchResult(2, 2))
            .round(new MatchRound("LaLiga round 28", LocalDateTime.of(2022, 2, 14, 21, 0, 0)))
            .build()
    );
  }
}
