package com.piter.match.management.rest;

import com.piter.match.management.model.Match;
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
public class HandledMatchService {

  private final CachedMatchService matchApiService;

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
