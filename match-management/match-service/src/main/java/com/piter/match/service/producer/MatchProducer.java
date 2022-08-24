package com.piter.match.service.producer;

import com.piter.match.service.domain.Match;

public interface MatchProducer {

  void sendMatch(Match match);
}
