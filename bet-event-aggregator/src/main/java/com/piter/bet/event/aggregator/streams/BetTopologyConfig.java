package com.piter.bet.event.aggregator.streams;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.Match;
import java.util.function.BiFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class BetTopologyConfig {

  @Bean
  public BiFunction<KStream<String, Bet>, KTable<String, Match>, KStream<String, Bet>> bets() {
    return (bets, matches) -> bets
        .filter((key, bet) -> validate(bet))
        .join(matches, this::joinByMatchId)
        .filterNot((k, v) -> v == null)
        .mapValues(this::fetchBetResult);
  }

  private boolean validate(Bet bet) {
    return true;
  }

  private Bet joinByMatchId(Bet bet, Match match) {
    Match betMatch = bet.getMatch();
    if (betMatch == null) {
//    if (betMatch == null || !betMatch.equals(match)) {
      return null;
    }
    logger.info("Joined bet: {}", bet); //TODO: set match to bet (because has result)
    return bet;
  }

  private Bet fetchBetResult(Bet bet) {
//    Optional.ofNullable(bet.getMatch().getResult())
//        .ifPresent();
    logger.info("Returned bet: {}", bet);
    return bet;
  }
}
