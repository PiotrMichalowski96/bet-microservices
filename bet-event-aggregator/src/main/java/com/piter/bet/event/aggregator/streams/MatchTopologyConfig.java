package com.piter.bet.event.aggregator.streams;

import static com.piter.bet.event.aggregator.streams.SerdeUtils.BET_JSON_SERDE;
import static com.piter.bet.event.aggregator.streams.SerdeUtils.MATCH_JSON_SERDE;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.util.BetIdGenerator;
import java.time.Duration;
import java.util.Objects;
import java.util.function.BiFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
class MatchTopologyConfig {

  private final Duration windowJoiningTime;

  MatchTopologyConfig(@Value("${bet.joining.window.hours}") Integer joiningTimeInHours) {
    windowJoiningTime = Duration.ofHours(joiningTimeInHours);
  }

  @Bean
  BiFunction<KStream<Long, Bet>, KStream<Long, Match>, KStream<String, Bet>> matches() {
    return (bets, matches) -> matches
        .peek((k, match) -> logger.debug("Key: {}, Received match: {}", k, ObjectUtils.defaultIfNull(match, "null")))
        .filter((k, match) -> isTombstoneMatchRecord(match))
        .peek((k, match) -> logger.debug("Key: {}, Tombstone record match", k))
        .mapValues(match -> Match.builder().build())
        .join(bets.peek((k, bet) -> logger.debug("Key: {}, Received bet: {}", k, bet)),
            (match, bet) -> bet,
            JoinWindows.ofTimeDifferenceWithNoGrace(windowJoiningTime),
            StreamJoined.with(Serdes.Long(), MATCH_JSON_SERDE, BET_JSON_SERDE))
        .map((k, bet) -> createTombstoneBetRecord(bet))
        .peek((k, tombstoneRecord) -> logger.debug("Key: {}, Tombstone record bet", k));
  }

  private boolean isTombstoneMatchRecord(Match match) {
    return Objects.isNull(match);
  }

  private KeyValue<String, Bet> createTombstoneBetRecord(Bet bet) {
    BetIdGenerator betIdGenerator = new BetIdGenerator(bet);
    String key = betIdGenerator.generateId();
    return new KeyValue<>(key, null);
  }
}
