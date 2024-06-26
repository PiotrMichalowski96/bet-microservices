package com.piter.bet.event.aggregator.streams;

import static com.piter.bet.event.aggregator.streams.SerdeUtils.BET_JSON_SERDE;
import static com.piter.bet.event.aggregator.streams.SerdeUtils.MATCH_JSON_SERDE;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.service.BetService;
import com.piter.bet.event.aggregator.util.BetIdGenerator;
import com.piter.bet.event.aggregator.validation.BetValidator;
import java.time.Duration;
import java.util.Optional;
import java.util.function.BiFunction;
import lombok.extern.slf4j.Slf4j;
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
class BetTopologyConfig {

  private final Duration windowJoiningTime;
  private final BetValidator betValidator;
  private final BetService betService;

  BetTopologyConfig(@Value("${bet.joining.window.hours}") Integer joiningTimeInHours,
      BetValidator betValidator, BetService betService) {
    this.windowJoiningTime = Duration.ofHours(joiningTimeInHours);
    this.betValidator = betValidator;
    this.betService = betService;
  }

  @Bean
  BiFunction<KStream<Long, Bet>, KStream<Long, Match>, KStream<String, Bet>> bets() {
    return (bets, matches) -> bets
        .peek((k, bet) -> logger.debug("Key: {}, Received bet: {}", k, bet))
        .filter((key, bet) -> betValidator.validate(bet))
        .peek((k, bet) -> logger.debug("Key: {}, Filtered bet: {}", k, bet))
        .join(matches.peek((k, match) -> logger.debug("Key: {}, Received match: {}", k, match)),
            this::joinByMatchId,
            JoinWindows.ofTimeDifferenceWithNoGrace(windowJoiningTime),
            StreamJoined.with(Serdes.Long(), BET_JSON_SERDE, MATCH_JSON_SERDE))
        .peek((k, bet) -> logger.debug("Key: {}, Joined bet: {}", k, bet))
        .filter((k, v) -> v != null)
        .map((key, bet) -> mapToBetWithId(bet))
        .mapValues(this::fetchBetResult)
        .peek((k, bet) -> logger.debug("Key: {}, Fetched bet: {}", k, bet));
  }

  private Bet joinByMatchId(Bet bet, Match match) {
    Match betMatch = bet.match();
    if (betMatch == null || !betMatch.equals(match)) {
      return null;
    }
    if (Optional.ofNullable(match.result()).isEmpty()) {
      return bet;
    }
    return mapToBetWithMatchResult(bet, match);
  }

  private Bet mapToBetWithMatchResult(Bet bet, Match match) {
    return Bet.builder()
        .id(bet.id())
        .matchPredictedResult(bet.matchPredictedResult())
        .match(match)
        .user(bet.user())
        .betResult(bet.betResult())
        .build();
  }

  private KeyValue<String, Bet> mapToBetWithId(Bet bet) {
    BetIdGenerator betIdGenerator = new BetIdGenerator(bet);
    String id = betIdGenerator.generateId();
    Bet betWithId = Bet.builder()
        .id(id)
        .matchPredictedResult(bet.matchPredictedResult())
        .match(bet.match())
        .user(bet.user())
        .betResult(bet.betResult())
        .build();
    return new KeyValue<>(id, betWithId);
  }

  private Bet fetchBetResult(Bet bet) {
    if (Optional.ofNullable(bet.match())
        .map(Match::result)
        .isEmpty()) {
      return bet;
    }
    return betService.fetchBetResult(bet);
  }
}
