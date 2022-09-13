package com.piter.match.management.service;

import com.piter.match.management.domain.Match;
import com.piter.match.management.domain.MatchResult;
import com.piter.match.management.domain.MatchRound;
import com.piter.match.management.model.view.MatchDashboardDetails;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class LoadMatchDetailsService {

  public MatchDashboardDetails loadMatchDetails() {
    List<Match> matches = List.of(
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

    return MatchDashboardDetails.builder()
        .currentPage(1)
        .totalPages(10)
        .matches(matches)
        .loadTime(LocalDateTime.now())
        .build();
  }
}
