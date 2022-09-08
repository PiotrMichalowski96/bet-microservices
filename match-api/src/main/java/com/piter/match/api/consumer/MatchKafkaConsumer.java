package com.piter.match.api.consumer;

import com.piter.match.api.domain.Match;
import com.piter.match.api.repository.MatchRepository;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MatchKafkaConsumer {

  private final Map<EventType, Consumer<Message<?>>> eventTypeProcessor;

  public MatchKafkaConsumer(MatchRepository matchRepository) {
    eventTypeProcessor = Map.of(
        EventType.INSERT, message -> {
          Match match = (Match) message.getPayload();
          matchRepository.save(match).subscribe();
          logger.debug("Inserted match: {}", match);
        },
        EventType.UPDATE, message -> {
          Match match = (Match) message.getPayload();
          matchRepository.save(match).subscribe();
          logger.debug("Updated match: {}", match);
        },
        EventType.DELETE, message -> {
          Long id = message.getHeaders().get(KafkaHeaders.RECEIVED_MESSAGE_KEY, Long.class);
          matchRepository.deleteById(Objects.requireNonNull(id)).subscribe();
          logger.debug("Deleted match id: {}", id);
        }
    );
  }

  @Bean
  public Consumer<Message<Match>> matches() {
    return matchMessage -> {
      EventType eventType = EventType.getEventType(matchMessage);
      eventTypeProcessor.get(eventType).accept(matchMessage);
    };
  }
}
