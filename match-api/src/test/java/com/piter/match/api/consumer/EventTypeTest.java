package com.piter.match.api.consumer;


import static org.assertj.core.api.Assertions.assertThat;

import com.piter.match.api.domain.Match;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;

class EventTypeTest {

  @ParameterizedTest
  @MethodSource("provideMessagesAndEvents")
  void shouldReturnCorrectEvents(Message<?> message, EventType expectedEvent) {
    //given input
    //when
    EventType actualEvent = EventType.getEventType(message);

    //then
    assertThat(actualEvent).isEqualTo(expectedEvent);
  }

  private static Stream<Arguments> provideMessagesAndEvents() {
    Message<?> matchMessageWithoutHeader = MessageBuilder.withPayload(Match.builder().build())
        .build();

    Message<?> matchMessageWithHeader = MessageBuilder.withPayload(Match.builder().build())
        .setHeader(KafkaHeaders.RECEIVED_MESSAGE_KEY, 1L)
        .build();

    Message<?> tombstoneMessage = MessageBuilder.withPayload(KafkaNull.INSTANCE)
        .setHeader(KafkaHeaders.RECEIVED_MESSAGE_KEY, 1L)
        .build();

    return Stream.of(
        Arguments.of(matchMessageWithoutHeader, EventType.INSERT),
        Arguments.of(matchMessageWithHeader, EventType.UPDATE),
        Arguments.of(tombstoneMessage, EventType.DELETE)
    );
  }
}