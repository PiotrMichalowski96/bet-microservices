package com.piter.match.api.consumer;

import static org.assertj.core.api.Assertions.assertThat;

import com.piter.api.commons.model.Match;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;

class MatchEventTypeTest {

  @ParameterizedTest
  @MethodSource("provideMessagesAndEvents")
  void shouldReturnCorrectEvents(Message<?> message, MatchEventType expectedEvent) {
    //given input
    //when
    MatchEventType actualEvent = MatchEventType.getEventType(message);

    //then
    assertThat(actualEvent).isEqualTo(expectedEvent);
  }

  private static Stream<Arguments> provideMessagesAndEvents() {
    Message<?> matchMessage = MessageBuilder.withPayload(Match.builder().build())
        .build();

    Message<?> tombstoneMessage = MessageBuilder.withPayload(KafkaNull.INSTANCE)
        .build();

    return Stream.of(
        Arguments.of(matchMessage, MatchEventType.UPSERT),
        Arguments.of(tombstoneMessage, MatchEventType.DELETE)
    );
  }
}