package com.piter.bet.event.aggregator.streams;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.Match;
import lombok.experimental.UtilityClass;
import org.springframework.kafka.support.serializer.JsonSerde;

@UtilityClass
public class SerdeUtils {

  public static final JsonSerde<Bet> BET_JSON_SERDE = new JsonSerde<>(Bet.class);
  public static final JsonSerde<Match> MATCH_JSON_SERDE = new JsonSerde<>(Match.class);
}
