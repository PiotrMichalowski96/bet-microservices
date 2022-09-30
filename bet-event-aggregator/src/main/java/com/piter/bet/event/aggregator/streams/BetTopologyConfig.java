package com.piter.bet.event.aggregator.streams;

import static com.piter.bet.event.aggregator.streams.SerdeUtils.BET_JSON_SERDE;
import static com.piter.bet.event.aggregator.streams.SerdeUtils.MATCH_JSON_SERDE;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.service.BetService;
import com.piter.bet.event.aggregator.validation.BetValidator;
import java.time.Duration;
import java.util.Optional;
import java.util.function.BiFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.JoinWindows;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.StreamJoined;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class BetTopologyConfig {

  private final Duration windowJoiningTime;
  private final BetValidator betValidator;
  private final BetService betService;

  public BetTopologyConfig(@Value("${bet.joining.window.hours}") Integer joiningTimeInHours,
      BetValidator betValidator, BetService betService) {
    this.windowJoiningTime = Duration.ofHours(joiningTimeInHours);
    this.betValidator = betValidator;
    this.betService = betService;
  }

  @Bean
  public BiFunction<KStream<Long, Bet>, KStream<Long, Match>, KStream<Long, Bet>> bets() {
    return (bets, matches) -> bets
        .peek((k, bet) -> logger.info("Key: {}, Received bet: {}", k, bet))
        .filter((key, bet) -> betValidator.validate(bet))
        .peek((k, bet) -> logger.info("Key: {}, Filtered bet: {}", k, bet))
        .join(matches.peek((k, match) -> logger.info("Key: {}, Received match: {}", k, match)),
            this::joinByMatchId,
            JoinWindows.ofTimeDifferenceWithNoGrace(windowJoiningTime),
            StreamJoined.with(Serdes.Long(), BET_JSON_SERDE, MATCH_JSON_SERDE))
        .peek((k, bet) -> logger.info("Key: {}, Joined bet: {}", k, bet))
        .filter((k, v) -> v != null)
        .mapValues(this::fetchBetResult)
        .peek((k, bet) -> logger.info("Key: {}, Fetched bet: {}", k, bet));
  }

  private Bet joinByMatchId(Bet bet, Match match) {
    Match betMatch = bet.getMatch();
    if (betMatch == null || !betMatch.equals(match)) {
      return null;
    }
    if (Optional.ofNullable(match.getResult()).isEmpty()) {
      return bet;
    }
    return mapToBetWithMatchResult(bet, match);
  }

  private Bet mapToBetWithMatchResult(Bet bet, Match match) {
    return Bet.builder()
        .id(bet.getId())
        .matchPredictedResult(bet.getMatchPredictedResult())
        .match(match)
        .user(bet.getUser())
        .betResult(bet.getBetResult())
        .build();
  }

  private Bet fetchBetResult(Bet bet) {
    if (Optional.ofNullable(bet.getMatch())
        .map(Match::getResult)
        .isEmpty()) {
      return bet;
    }
    return betService.fetchBetResult(bet);
  }
}
