package com.piter.bet.event.aggregator.domain;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Value
@Builder
public class Bet {
  Long id;
  Integer homeTeamGoalBet;
  Integer awayTeamGoalBet;
  Match match;
  User user;
  BetResults betResults;
}
