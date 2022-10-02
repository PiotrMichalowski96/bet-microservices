package com.piter.bet.api.consumer;

import com.piter.bet.api.domain.Bet;
import com.piter.bet.api.exception.BetKafkaException;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;

@Getter
@RequiredArgsConstructor
public enum BetEventType {

  SAVE(message -> hasPayloadType(message, Bet.class)),
  // kafka convention takes record with null value (tombstone record) as delete operation
  // see (https://www.linkedin.com/pulse/tombstone-record-kafka-under-hood-sumon-mal/)
  DELETE(message -> hasPayloadType(message, KafkaNull.class)),;

  private final Predicate<Message<?>> eventTypePredicate;

  private static boolean hasPayloadType(Message<?> message, Class<?> payloadType) {
    return payloadType.isInstance(message.getPayload());
  }

  public static BetEventType getEventType(Message<?> message) {
    return Stream.of(BetEventType.values())
        .filter(betEventType -> betEventType.getEventTypePredicate().test(message))
        .findFirst()
        .orElseThrow(BetKafkaException::eventTypeNotFound);
  }
}
