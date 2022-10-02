package com.piter.match.api.util;

import com.piter.match.api.domain.Match;
import com.piter.match.api.domain.MatchResult;
import com.piter.match.api.domain.MatchRound;
import java.time.LocalDateTime;
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
            .startTime(LocalDateTime.of(2022, 2, 17, 21, 0, 0))
            .result(new MatchResult(4, 0))
            .round(new MatchRound("LaLiga round 30", LocalDateTime.of(2022, 2, 14, 21, 0, 0)))
            .build(),
        Match.builder()
            .id(2L)
            .homeTeam("Sevilla")
            .awayTeam("Athletic Bilbao")
            .startTime(LocalDateTime.of(2022, 2, 8, 19, 0, 0))
            .result(new MatchResult(2, 1))
            .round(new MatchRound("LaLiga round 29", LocalDateTime.of(2022, 2, 7, 21, 0, 0)))
            .build(),
        Match.builder()
            .id(3L)
            .homeTeam("Atletico Madrid")
            .awayTeam("Valencia")
            .startTime(LocalDateTime.of(2022, 2, 15, 21, 0, 0))
            .result(new MatchResult(2, 2))
            .round(new MatchRound("LaLiga round 30", LocalDateTime.of(2022, 2, 14, 21, 0, 0)))
            .build()
    );
  }
}
