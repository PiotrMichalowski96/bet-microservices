package com.piter.bet.event.aggregator.util;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.domain.User;
import com.piter.bet.event.aggregator.exception.BetAggregatorException;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;

/**
 * The idea is to generate Bet identification based on match ID and user
 */
public class BetIdGenerator {

  private final Long matchId;
  private final User user;

  public BetIdGenerator(Bet bet) {
    this.matchId = Optional.ofNullable(bet.match())
        .map(Match::id)
        .orElseThrow(() -> BetAggregatorException.missingFieldException("Match ID", bet));
    this.user = Optional.ofNullable(bet.user())
        .orElseThrow(() -> BetAggregatorException.missingFieldException("User", bet));
  }

  public String generateId() {
    String concatMatchIdAndUser = StringUtils.join(matchId,
        user.firstName(),
        user.lastName(),
        user.nickname());

    UUID uuid = UUID.nameUUIDFromBytes(concatMatchIdAndUser.getBytes());
    return uuid.toString();
  }
}
