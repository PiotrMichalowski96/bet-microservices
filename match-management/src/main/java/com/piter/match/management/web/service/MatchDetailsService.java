package com.piter.match.management.web.service;

import com.piter.match.management.domain.Match;
import com.piter.match.management.web.model.MatchDetails;
import com.piter.match.management.rest.HandledCallerService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MatchDetailsService {

  private static final String ORDER_BY_MATCH_TIME = "match-time";

  private final HandledCallerService handledCallerService;

  public List<Match> loadMatches() {
    return handledCallerService.getMatchList(ORDER_BY_MATCH_TIME);
  }

  public MatchDetails findMatch(Long matchId) {
    String findErrorMessage = "Can not find match.";
    return handledCallerService.getMatch(matchId)
        .map(MatchDetails::new)
        .orElse(new MatchDetails(findErrorMessage));
  }

  public MatchDetails saveMatch(Match match) {
    String saveErrorMessage = "Can not save match.";
    return handledCallerService.saveMatch(match)
        .map(MatchDetails::new)
        .orElse(new MatchDetails(saveErrorMessage));
  }
}
