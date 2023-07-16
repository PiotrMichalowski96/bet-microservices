package com.piter.match.api.producer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.piter.api.commons.domain.Match;
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
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;

@ExtendWith(MockitoExtension.class)
class MatchKafkaProducerTest {

  private static final String PRODUCER_BINDING = "matches-out-0";

  @Mock
  private StreamBridge streamBridge;

  @Captor
  private ArgumentCaptor<Message<Match>> messageCaptor;

  private MatchKafkaProducer matchKafkaProducer;

  @BeforeEach
  void init() {
    matchKafkaProducer = new MatchKafkaProducer(PRODUCER_BINDING, streamBridge);
  }

  @Test
  void shouldSendSaveMatchEvent() {
    //given
    var matchId = 1L;
    var match = Match.builder()
        .id(matchId)
        .build();

    Message<?> expectedMessage = MessageBuilder
        .withPayload(match)
        .setHeader(KafkaHeaders.KEY, matchId)
        .build();

    //when
    matchKafkaProducer.sendSaveMatchEvent(match);

    //then
    assertMessageWasSent(expectedMessage);
  }

  @Test
  void shouldSendDeleteMatchEvent() {
    //given
    var matchId = 1L;
    var match = Match.builder()
        .id(matchId)
        .build();

    Message<?> expectedMessage = MessageBuilder
        .withPayload(KafkaNull.INSTANCE)
        .setHeader(KafkaHeaders.KEY, matchId)
        .build();

    //when
    matchKafkaProducer.sendDeleteMatchEvent(match);

    //then
    assertMessageWasSent(expectedMessage);
  }

  private void assertMessageWasSent(Message<?> expectedMessage) {
    verify(streamBridge).send(eq(PRODUCER_BINDING), messageCaptor.capture());

    Message<Match> actualMessage = messageCaptor.getValue();
    assertThat(actualMessage).usingRecursiveComparison()
        .ignoringFields("headers.id", "headers.timestamp") //kafka key is only important header to check
        .isEqualTo(expectedMessage);
  }
}