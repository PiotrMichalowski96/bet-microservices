package com.piter.bet.event.aggregator.streams;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.Match;
import lombok.experimental.UtilityClass;
import org.springframework.kafka.support.serializer.JsonSerde;

@UtilityClass
class SerdeUtils {

  static final JsonSerde<Bet> BET_JSON_SERDE = new JsonSerde<>(Bet.class);
  static final JsonSerde<Match> MATCH_JSON_SERDE = new JsonSerde<>(Match.class);
}
