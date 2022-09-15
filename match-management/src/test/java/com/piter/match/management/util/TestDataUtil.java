package com.piter.match.management.util;

import com.piter.match.management.domain.Match;
import com.piter.match.management.domain.MatchResult;
import com.piter.match.management.domain.MatchRound;
import java.time.LocalDateTime;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestDataUtil {

  public static Match createMatch(Long id) {
    return Match.builder()
        .id(1L)
        .homeTeam("FC Barcelona")
        .awayTeam("Real Madrid")
        .startTime(LocalDateTime.of(2022, 2, 17, 21, 0, 0))
        .result(new MatchResult(4, 0))
        .round(new MatchRound("LaLiga round 30", LocalDateTime.of(2022, 2, 14, 21, 0, 0)))
        .build();
  }

  public static List<Match> createMatchList() {
    return List.of(createMatch(1L), createMatch(2L));
  }

}
