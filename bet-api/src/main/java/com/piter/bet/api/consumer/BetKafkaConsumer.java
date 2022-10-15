package com.piter.bet.api.consumer;

import com.piter.api.commons.domain.Bet;
import com.piter.bet.api.exception.BetKafkaException;
import com.piter.bet.api.repository.BetRepository;
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
    Bet bet = (Bet) betMessage.getPayload();
    betRepository.save(bet).subscribe(
        savedBet -> logger.debug("Saved bet: {}", bet)
    );
  }

  private void deleteBet(Message<?> tombstoneMessage) {
    Long id = Optional.ofNullable(tombstoneMessage.getHeaders().get(KafkaHeaders.RECEIVED_MESSAGE_KEY, Long.class))
        .orElseThrow(() -> new BetKafkaException("Delete event does not have Kafka key ID"));
    betRepository.deleteById(id).subscribe();
    logger.debug("Deleted bet id: {}", id);
  }
}
