package com.piter.match.api.producer;

import com.piter.api.commons.domain.Match;
import com.piter.api.commons.producer.KafkaMessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MatchKafkaProducer extends KafkaMessageProducer {

  public MatchKafkaProducer(@Value("${match.producer.binding}") String producerBinding, StreamBridge streamBridge) {
    super(producerBinding, streamBridge);
  }

  public Match sendSaveMatchEvent(Match match) {
    sendEvent(match::getId, match);
    logger.debug("Sent match to save: {}", match);
    return match;
  }

  public void sendDeleteMatchEvent(Match match) {
    sendEvent(match::getId, KafkaNull.INSTANCE);
    logger.debug("Sent match to delete: {}", match);
  }
}
