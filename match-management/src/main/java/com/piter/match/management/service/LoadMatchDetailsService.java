package com.piter.match.management.service;

import com.piter.match.management.model.Match;
import com.piter.match.management.model.MatchResult;
import com.piter.match.management.model.MatchRound;
import com.piter.match.management.rest.HandledMatchService;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoadMatchDetailsService {

  private static final String ORDER_BY_MATCH_TIME = "match-time";

  @Value("${mock.enabled:true}")
  private final Boolean mockEnabled;
  private final HandledMatchService handledMatchService;

  public List<Match> loadMatches() {
    if (mockEnabled) {
      return mockMatches();
    }
    return handledMatchService.getMatchList(ORDER_BY_MATCH_TIME);
  }

  private List<Match> mockMatches() {
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
