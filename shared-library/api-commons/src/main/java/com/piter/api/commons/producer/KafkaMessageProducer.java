package com.piter.api.commons.producer;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

@RequiredArgsConstructor
public abstract class KafkaMessageProducer {

  private final String producerBinding;
  private final StreamBridge streamBridge;

  protected void sendEvent(Supplier<Long> keySupplier, Object payload) {
    Long key = keySupplier.get();

    Message<?> event = MessageBuilder
        .withPayload(payload)
        .setHeader(KafkaHeaders.KEY, key)
        .build();

    streamBridge.send(producerBinding, event);
  }
}
