package com.piter.bet.api.consumer;

import com.piter.api.commons.domain.Bet;
import com.piter.bet.api.exception.BetKafkaException;
import com.piter.bet.api.repository.BetRepository;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BetKafkaConsumer {

  private static final Duration DB_TIMEOUT = Duration.ofSeconds(1);

  private final BetRepository betRepository;
  private final Map<BetEventType, Consumer<Message<?>>> eventTypeProcessor;

  public BetKafkaConsumer(BetRepository betRepository) {
    this.betRepository = betRepository;
    this.eventTypeProcessor = Map.of(
        BetEventType.SAVE, this::saveBet,
        BetEventType.DELETE, this::deleteBet
    );
  }

  @Bean
  public Consumer<Message<Bet>> bets() {
    return betMessage -> {
      BetEventType betEventType = BetEventType.getEventType(betMessage);
      eventTypeProcessor.get(betEventType).accept(betMessage);
    };
  }

  private void saveBet(Message<?> betMessage) {
    logReceivedMessage(betMessage);
    Bet bet = (Bet) betMessage.getPayload();
    betRepository.save(bet).block(DB_TIMEOUT); //block is used to ensure saving order
  }

  private void logReceivedMessage(Message<?> betMessage) {
    Optional.ofNullable(betMessage.getHeaders().get(KafkaHeaders.RECEIVED_KEY, String.class))
        .ifPresent(key -> {
          Bet bet = (Bet) betMessage.getPayload();
          logger.debug("Received bet to save. Key: {} bet: {}", key, bet);
        });
  }

  private void deleteBet(Message<?> tombstoneMessage) {
    String id = Optional.ofNullable(tombstoneMessage.getHeaders().get(KafkaHeaders.RECEIVED_KEY, String.class))
        .orElseThrow(() -> new BetKafkaException("Delete event does not have Kafka key ID"));
    betRepository.deleteById(id).block(DB_TIMEOUT); //block is used to ensure delete order
    logger.debug("Deleted bet id: {}", id);
  }
}
