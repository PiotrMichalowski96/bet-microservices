package com.piter.match.management.rest;

import com.piter.match.management.domain.Match;
import com.piter.match.management.domain.MatchResult;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HandledCallerService {

  private final CallerService matchApiService;

  public Optional<Match> getMatch(Long matchId) {
    return callHandled(() -> matchApiService.callMatch(matchId));
  }

  private <T> Optional<T> callHandled(Supplier<T> call) {
    try {
      return Optional.of(call.get());
    } catch (Exception e) {
      logger.error("Error during call: {}", e);
      return Optional.empty();
    }
  }

  public Optional<Match> saveMatch(Match match) {
    Match validMatch = validateMatchResult(match);
    return callHandled(() -> matchApiService.saveMatch(validMatch));
  }

  private Match validateMatchResult(Match match) {
    if (isValidMatchResult(match)) {
      return match;
    }
    match.setResult(null);
    return match;
  }

  private boolean isValidMatchResult(Match match) {
    MatchResult matchResult = match.getResult();
    return matchResult.getAwayTeamGoals() != null && matchResult.getHomeTeamGoals() != null;
  }

  public List<Match> getMatchList(String order) {
    return callListHandled(() -> matchApiService.callMatchList(order));
  }

  private <T> List<T> callListHandled(Supplier<List<T>> call) {
    try {
      return call.get();
    } catch (Exception e) {
      logger.error("Error during call: {}", e);
      return Collections.emptyList();
    }
  }
}
