package com.piter.match.api.producer;

import com.piter.match.api.domain.Match;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchKafkaProducer implements MatchProducer {

  @Value("${match.producer.binding}")
  private final String matchBinding;
  private final StreamBridge streamBridge;

  @Override
  public Match sendSaveMatchEvent(Match match) {
    Long key = match.getId();
    Message<Match> matchMessage = MessageBuilder
        .withPayload(match)
        .setHeader(KafkaHeaders.MESSAGE_KEY, key)
        .build();

    streamBridge.send(matchBinding, matchMessage);
    logger.debug("Sent match to save: {}", match);
    return match;
  }

  @Override
  public void sendDeleteMatchEvent(Match match) {
    Long key = match.getId();
    Message<KafkaNull> tombstoneRecord = MessageBuilder
        .withPayload(KafkaNull.INSTANCE)
        .setHeader(KafkaHeaders.MESSAGE_KEY, key)
        .build();

    streamBridge.send(matchBinding, tombstoneRecord);
    logger.debug("Sent match to delete: {}", match);
  }
}
