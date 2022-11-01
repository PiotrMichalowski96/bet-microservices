package com.piter.match.management.rest;

import com.piter.match.management.domain.Match;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CallerService {

  private final MatchApiClient matchApiClient;

  @Cacheable(value = "match", key = "#matchId")
  public Match callMatch(Long matchId) {
    Match match = matchApiClient.findMatchById(matchId);
    logger.debug("Received match: {}", match);
    return match;
  }

  public List<Match> callMatchList(String order) {
    List<Match> matches = matchApiClient.findMatches(order);
    matches.forEach(match -> logger.debug("Received match: {}", match));
    return matches;
  }

  @CacheEvict(value = "match", key = "#match.id", condition = "#match.id != null")
  public Match saveMatch(Match match) {
    Match savedMatch = matchApiClient.saveMatch(match);
    logger.debug("Saved match: {}", match);
    return savedMatch;
  }

  @CacheEvict(value = "match", key = "#matchId")
  public void deleteMatch(Long matchId) {
    matchApiClient.deleteMatch(matchId);
  }
}
