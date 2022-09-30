package com.piter.bet.api.consumer;

import com.piter.bet.api.domain.Bet;
import com.piter.bet.api.repository.BetRepository;
import com.piter.bet.api.util.BetIdGenerator;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BetKafkaConsumer {

  private final BetRepository betRepository;

  @Bean
  public Consumer<Message<Bet>> bets() {
    return betMessage -> {
      Bet bet = betMessage.getPayload();

      var betIdGenerator = new BetIdGenerator(bet);
      Long betId = betIdGenerator.generateId();

      Bet betWithId = map(bet, betId);
      betRepository.save(betWithId);
    };
  }

  private Bet map(Bet bet, Long id) {
    return Bet.builder()
        .id(id)
        .matchPredictedResult(bet.getMatchPredictedResult())
        .match(bet.getMatch())
        .user(bet.getUser())
        .betResult(bet.getBetResult())
        .build();
  }
}
