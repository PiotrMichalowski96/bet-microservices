package com.piter.bet.api.producer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.piter.api.commons.domain.Bet;
import com.piter.api.commons.domain.Match;
import com.piter.bet.api.exception.MissingFieldException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;

@ExtendWith(MockitoExtension.class)
class BetKafkaProducerTest {

  private static final String PRODUCER_BINDING = "bets-out-0";

  @Mock
  private StreamBridge streamBridge;

  @Captor
  private ArgumentCaptor<Message<Bet>> messageCaptor;

  private BetKafkaProducer betKafkaProducer;

  @BeforeEach
  void init() {
    betKafkaProducer = new BetKafkaProducer(PRODUCER_BINDING, streamBridge);
  }

  @Test
  void shouldSendSaveBetEvent() {
    //given
    var matchId = 123L;
    var bet = Bet.builder()
        .match(Match.builder()
            .id(matchId)
            .build())
        .build();

    Message<?> expectedMessage = MessageBuilder
        .withPayload(bet)
        .setHeader(KafkaHeaders.KEY, matchId)
        .build();

    //when
    betKafkaProducer.sendSaveBetEvent(bet);

    //then
    assertMessageWasSent(expectedMessage);
  }

  private void assertMessageWasSent(Message<?> expectedMessage) {
    verify(streamBridge).send(eq(PRODUCER_BINDING), messageCaptor.capture());

    Message<Bet> actualMessage = messageCaptor.getValue();
    assertThat(actualMessage).usingRecursiveComparison()
        .ignoringFields("headers.id", "headers.timestamp") //kafka key is only important header to check
        .isEqualTo(expectedMessage);
  }

  @Test
  void shouldNotSendEventBecauseBetIsInvalid() {
    //given
    var betWithoutMatchId = Bet.builder()
        .match(Match.builder().build())
        .build();

    //whenThen
    assertThatThrownBy(() -> betKafkaProducer.sendSaveBetEvent(betWithoutMatchId))
        .isInstanceOf(MissingFieldException.class);
  }
}