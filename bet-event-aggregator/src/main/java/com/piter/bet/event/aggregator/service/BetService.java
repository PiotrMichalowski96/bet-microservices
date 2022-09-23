package com.piter.bet.event.aggregator.service;

import com.piter.bet.event.aggregator.domain.Bet;

public interface BetService {

  Bet fetchBetResult(Bet bet);
}
