package com.piter.bet.api.util;

import com.piter.bet.api.domain.Bet;
import com.piter.bet.api.domain.Match;
import com.piter.bet.api.domain.User;
import com.piter.bet.api.exception.MissingFieldException;
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
    this.matchId = Optional.ofNullable(bet.getMatch())
        .map(Match::getId)
        .orElseThrow(() -> new MissingFieldException("Match ID is missing", bet));
    this.user = Optional.ofNullable(bet.getUser())
        .orElseThrow(() -> new MissingFieldException("User is missing", bet));
  }

  public Long generateId() {
    String concatMatchIdAndUser = StringUtils.join(matchId,
        user.getFirstName(),
        user.getLastName(),
        user.getNickname());

    UUID uuid = UUID.nameUUIDFromBytes(concatMatchIdAndUser.getBytes());
    return uuid.getMostSignificantBits();
  }
}
