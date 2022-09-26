package com.piter.bet.event.aggregator.exception;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.domain.MatchResult;

public class BetAggregatorException extends RuntimeException {

  public BetAggregatorException(String message) {
    super(message);
  }

  public static BetAggregatorException cannotPredictException(Bet bet) {
    var message = String.format("Can not predict based on this Bet: %s", bet);
    return new BetAggregatorException(message);
  }

  public static BetAggregatorException cannotRetrieveResultException(Match match) {
    var message = String.format("Can not retrieve result from match: %s", match);
    return new BetAggregatorException(message);
  }

  public static BetAggregatorException cannotRetrieveResultException(MatchResult matchResult) {
    var message = String.format("Can not retrieve result from match result: %s", matchResult);
    return new BetAggregatorException(message);
  }
}
