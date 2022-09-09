package com.piter.match.api.consumer;

import com.piter.match.api.domain.Match;
import com.piter.match.api.exception.MatchKafkaException;
import com.piter.match.api.repository.MatchRepository;
import com.piter.match.api.service.SequenceGeneratorService;
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
  private final SequenceGeneratorService sequenceGeneratorService;
  private final Map<EventType, Consumer<Message<?>>> eventTypeProcessor;

  public MatchKafkaConsumer(MatchRepository matchRepository,
      SequenceGeneratorService sequenceGeneratorService) {

    this.matchRepository = matchRepository;
    this.sequenceGeneratorService = sequenceGeneratorService;
    this.eventTypeProcessor = Map.of(
        EventType.INSERT, this::saveMatch,
        EventType.UPDATE, this::updateMatch,
        EventType.DELETE, this::deleteMatch
    );
  }

  @Bean
  public Consumer<Message<Match>> matches() {
    return matchMessage -> {
      EventType eventType = EventType.getEventType(matchMessage);
      eventTypeProcessor.get(eventType).accept(matchMessage);
    };
  }

  private void saveMatch(Message<?> matchMessage) {
    Match match = (Match) matchMessage.getPayload();
    sequenceGeneratorService.generateSequence(Match.SEQUENCE_NAME)
        .map(id -> map(match, id))
        .subscribe(
            matchWithId -> matchRepository.save(matchWithId).subscribe(
                savedMatch -> logger.debug("Inserted match: {}", savedMatch)
            )
        );
  }

  private Match map(Match match, Long id) {
    return Match.builder()
        .id(id)
        .homeTeam(match.getHomeTeam())
        .awayTeam(match.getAwayTeam())
        .startTime(match.getStartTime())
        .result(match.getResult())
        .round(match.getRound())
        .build();
  }

  private void updateMatch(Message<?> matchMessage) {
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
