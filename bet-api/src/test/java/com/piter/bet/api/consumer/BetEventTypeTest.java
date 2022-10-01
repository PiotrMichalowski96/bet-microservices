package com.piter.bet.api.consumer;

import static org.assertj.core.api.Assertions.assertThat;

import com.piter.bet.api.domain.Bet;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;

class BetEventTypeTest {

  @ParameterizedTest
  @MethodSource("provideMessagesAndEventTypes")
  void shouldReturnCorrectEvent(Message<?> message, BetEventType expectedEventType) {
    //given
    //when
    BetEventType actualEventType = BetEventType.getEventType(message);

    //then
    assertThat(actualEventType).isEqualTo(expectedEventType);
  }

  private static Stream<Arguments> provideMessagesAndEventTypes() {
    Message<?> betMessage = MessageBuilder.withPayload(Bet.builder().build())
        .build();

    Message<?> tombstoneMessage = MessageBuilder.withPayload(KafkaNull.INSTANCE)
        .build();

    return Stream.of(
        Arguments.of(betMessage, BetEventType.SAVE),
        Arguments.of(tombstoneMessage, BetEventType.DELETE)
    );
  }
}