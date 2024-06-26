package com.piter.bet.api.producer;

import com.piter.api.commons.domain.Bet;
import com.piter.api.commons.domain.Match;
import com.piter.api.commons.producer.KafkaMessageProducer;
import com.piter.bet.api.exception.MissingFieldException;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BetKafkaProducer extends KafkaMessageProducer {

  public BetKafkaProducer(@Value("${bet.producer.binding}") String betBinding, StreamBridge streamBridge) {
    super(betBinding, streamBridge);
  }

  public Bet sendSaveBetEvent(Bet bet) {
    Supplier<Long> keySupplier = () -> Optional.ofNullable(bet.match())
        .map(Match::id)
        .orElseThrow(() -> new MissingFieldException("Match ID is missing", bet));
    sendEvent(keySupplier, bet);
    logger.debug("Sent bet to save: {}", bet);
    return bet;
  }
}
