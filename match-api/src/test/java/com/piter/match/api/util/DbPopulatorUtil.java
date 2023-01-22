package com.piter.match.api.util;

import com.piter.api.commons.domain.Match;
import com.piter.match.api.repository.MatchRepository;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class DbPopulatorUtil {

  public static void fillDatabaseIfEmpty(MatchRepository matchRepository, List<Match> matches) {
    List<Match> existingMatches = matchRepository.findAll()
        .collectList()
        .block();
    if (existingMatches == null || existingMatches.isEmpty()) {
      fillDatabase(matchRepository, matches);
    }
  }

  private static void fillDatabase(MatchRepository matchRepository, List<Match> matches) {
    matches.forEach(match -> matchRepository.save(match).block());
  }
}
