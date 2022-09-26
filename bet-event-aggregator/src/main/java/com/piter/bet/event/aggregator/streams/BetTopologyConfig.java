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
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
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
  public BiFunction<KStream<String, Bet>, KStream<String, Match>, KStream<String, Bet>> bets() {
    return (bets, matches) -> bets
        .peek((k, bet) -> logger.info("Received bet: {}", bet))
        .filter((key, bet) -> betValidator.validate(bet))
        .peek((k, bet) -> logger.info("Filtered bet: {}", bet))
        .join(matches,
            this::joinByMatchId,
            JoinWindows.ofTimeDifferenceWithNoGrace(windowJoiningTime),
            StreamJoined.with(Serdes.String(), BET_JSON_SERDE, MATCH_JSON_SERDE))
        .peek((k, bet) -> logger.info("Joined bet: {}", bet))
        .filterNot((k, v) -> v == null)
        .mapValues(this::fetchBetResult)
        .peek((k, bet) -> logger.info("Fetched bet: {}", bet));
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
        .betResults(bet.getBetResults())
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
