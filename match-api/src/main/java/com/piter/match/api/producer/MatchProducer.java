package com.piter.match.api.producer;

import com.piter.match.api.domain.Match;

public interface MatchProducer {

  Match sendSaveMatchEvent(Match match);

  void sendDeleteMatchEvent(Match match);
}
