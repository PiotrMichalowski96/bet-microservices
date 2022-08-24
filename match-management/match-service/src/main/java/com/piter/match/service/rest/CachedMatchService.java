package com.piter.match.service.rest;

import com.piter.match.service.domain.Match;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CachedMatchService {

  private final MatchApiClient matchApiClient;

  @Cacheable("match")
  public Match callMatch(Long matchId) {
    Match match = matchApiClient.findMatchById(matchId);
    logger.debug("Received match: {}", match);
    return match;
  }

  @Cacheable("match-list")
  public List<Match> callMatchList(String order) {
    List<Match> matches = matchApiClient.findMatches(order);
    matches.forEach(match -> logger.debug("Received match: {}", match));
    return matches;
  }
}
