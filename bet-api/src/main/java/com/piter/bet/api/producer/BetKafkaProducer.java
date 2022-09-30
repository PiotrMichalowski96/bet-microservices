package com.piter.bet.api.producer;

import com.piter.bet.api.domain.Bet;
import com.piter.bet.api.domain.Match;
import com.piter.bet.api.exception.MissingFieldException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class BetKafkaProducer {

  @Value("${bet.producer.binding}")
  private final String betBinding;
  private final StreamBridge streamBridge;

  public Bet sendSaveBetEvent(Bet bet) {
    Long key = Optional.ofNullable(bet.getMatch())
        .map(Match::getId)
        .orElseThrow(() -> new MissingFieldException("Match ID is missing", bet));

    Message<Bet> betMessage = MessageBuilder
        .withPayload(bet)
        .setHeader(KafkaHeaders.MESSAGE_KEY, key)
        .build();

    streamBridge.send(betBinding, betMessage);
    logger.debug("Sent bet to save: {}", bet);
    return bet;
  }
}
