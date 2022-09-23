package com.piter.bet.event.aggregator.prediction;

import com.piter.bet.event.aggregator.domain.Bet;

@FunctionalInterface
public interface Prediction {

  boolean predict(Bet bet);
}
