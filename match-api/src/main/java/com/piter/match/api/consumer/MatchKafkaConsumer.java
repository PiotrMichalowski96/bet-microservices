package com.piter.match.api.consumer;

import com.piter.api.commons.event.MatchEvent;
import com.piter.match.api.exception.MatchKafkaException;
import com.piter.match.api.repository.MatchRepository;
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
public class MatchKafkaConsumer {

  private static final Duration DB_TIMEOUT = Duration.ofSeconds(1);

  private final MatchRepository matchRepository;
  private final Map<MatchEventType, Consumer<Message<?>>> eventTypeProcessor;

  public MatchKafkaConsumer(MatchRepository matchRepository) {
    this.matchRepository = matchRepository;
    this.eventTypeProcessor = Map.of(
        MatchEventType.UPSERT, this::upsertMatch,
        MatchEventType.DELETE, this::deleteMatch
    );
  }

  @Bean
  public Consumer<Message<MatchEvent>> matches() {
    return matchMessage -> {
      MatchEventType matchEventType = MatchEventType.getEventType(matchMessage);
      eventTypeProcessor.get(matchEventType).accept(matchMessage);
    };
  }

  private void upsertMatch(Message<?> matchMessage) {
    logReceivedMessage(matchMessage);
    MatchEvent matchEvent = (MatchEvent) matchMessage.getPayload();
    matchRepository.save(matchEvent.toMatch()).block(DB_TIMEOUT); //block is used to ensure saving order
  }

  private void logReceivedMessage(Message<?> matchMessage) {
    Optional.ofNullable(matchMessage.getHeaders().get(KafkaHeaders.RECEIVED_KEY, Long.class))
        .ifPresent(key -> {
          MatchEvent matchEvent = (MatchEvent) matchMessage.getPayload();
          logger.debug("Received match to save. Key: {} match: {}", key, matchEvent);
        });
  }

  private void deleteMatch(Message<?> tombstoneMessage) {
    Long id = Optional.ofNullable(tombstoneMessage.getHeaders().get(KafkaHeaders.RECEIVED_KEY, Long.class))
        .orElseThrow(() -> new MatchKafkaException("Delete event does not have Kafka key ID"));
    matchRepository.deleteById(id).block(DB_TIMEOUT); //block is used to ensure delete order
    logger.debug("Deleted match id: {}", id);
  }
}
