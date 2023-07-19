package com.piter.match.api.consumer;

import com.piter.api.commons.domain.Match;
import com.piter.match.api.exception.MatchKafkaException;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;

@Getter
@RequiredArgsConstructor
public enum MatchEventType {

  SAVE(message -> hasPayloadType(message, Match.class)),
  // kafka convention takes record with null value (tombstone record) as delete operation
  // see (https://www.linkedin.com/pulse/tombstone-record-kafka-under-hood-sumon-mal/)
  DELETE(message -> hasPayloadType(message, KafkaNull.class)),;

  private final Predicate<Message<?>> eventTypePredicate;

  private static boolean hasPayloadType(Message<?> message, Class<?> payloadType) {
    if (message == null) {
      return false;
    }
    return payloadType.isInstance(message.getPayload());
  }

  public static MatchEventType getEventType(Message<?> message) {
    return Stream.of(MatchEventType.values())
        .filter(matchEventType -> matchEventType.getEventTypePredicate().test(message))
        .findFirst()
        .orElseThrow(MatchKafkaException::eventTypeNotFound);
  }
}
