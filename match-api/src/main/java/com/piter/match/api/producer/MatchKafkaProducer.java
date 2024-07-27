package com.piter.match.api.producer;

import com.piter.api.commons.event.MatchEvent;
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

  public MatchEvent sendSaveMatchEvent(MatchEvent matchEvent) {
    sendEvent(matchEvent::id, matchEvent);
    logger.debug("Sent match to save: {}", matchEvent);
    return matchEvent;
  }

  public void sendDeleteMatchEvent(MatchEvent matchEvent) {
    sendEvent(matchEvent::id, KafkaNull.INSTANCE);
    logger.debug("Sent match to delete: {}", matchEvent);
  }
}
