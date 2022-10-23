package com.piter.match.api.consumer;

import com.piter.api.commons.domain.Match;
import com.piter.match.api.exception.MatchKafkaException;
import com.piter.match.api.repository.MatchRepository;
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

  private final MatchRepository matchRepository;
  private final Map<MatchEventType, Consumer<Message<?>>> eventTypeProcessor;

  public MatchKafkaConsumer(MatchRepository matchRepository) {
    this.matchRepository = matchRepository;
    this.eventTypeProcessor = Map.of(
        MatchEventType.SAVE, this::saveMatch,
        MatchEventType.DELETE, this::deleteMatch
    );
  }

  @Bean
  public Consumer<Message<Match>> matches() {
    return matchMessage -> {
      MatchEventType matchEventType = MatchEventType.getEventType(matchMessage);
      eventTypeProcessor.get(matchEventType).accept(matchMessage);
    };
  }

  private void saveMatch(Message<?> matchMessage) {
    Match match = (Match) matchMessage.getPayload();
    matchRepository.save(match).subscribe(
        savedMatch -> logger.debug("Updated match: {}", savedMatch)
    );
  }

  private void deleteMatch(Message<?> tombstoneMessage) {
    Long id = Optional.ofNullable(tombstoneMessage.getHeaders().get(KafkaHeaders.RECEIVED_MESSAGE_KEY, Long.class))
        .orElseThrow(() -> new MatchKafkaException("Delete event does not have Kafka key ID"));
    matchRepository.deleteById(id).subscribe();
    logger.debug("Deleted match id: {}", id);
  }
}
