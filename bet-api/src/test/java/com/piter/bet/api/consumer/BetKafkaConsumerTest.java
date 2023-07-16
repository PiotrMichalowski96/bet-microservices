package com.piter.bet.api.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.piter.api.commons.domain.Bet;
import com.piter.bet.api.repository.BetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class BetKafkaConsumerTest {

  @Mock
  private BetRepository betRepository;

  @Captor
  private ArgumentCaptor<Bet> betArgumentCaptor;

  @InjectMocks
  private BetKafkaConsumer betKafkaConsumer;

  @Test
  void shouldSaveBet() {
    //given
    var bet = Bet.builder()
        .id("1")
        .build();

    var betMessage = MessageBuilder
        .withPayload(bet)
        .build();

    mockSave(bet);

    //when
    betKafkaConsumer.bets().accept(betMessage);

    //then
    verify(betRepository).save(betArgumentCaptor.capture());
    Bet savedBet = betArgumentCaptor.getValue();
    assertThat(savedBet).isEqualTo(bet);
  }

  @Test
  void shouldDeleteBet() {
    //given
    var messageKey = "123";
    Message tombstoneMessage = MessageBuilder
        .withPayload(KafkaNull.INSTANCE)
        .setHeader(KafkaHeaders.RECEIVED_KEY, messageKey)
        .build();

    mockDelete();

    //when
    betKafkaConsumer.bets().accept(tombstoneMessage);

    //then
    verify(betRepository).deleteById(messageKey);
  }

  private void mockSave(Bet bet) {
    when(betRepository.save(bet)).thenReturn(Mono.just(bet));
  }

  private void mockDelete() {
    when(betRepository.deleteById(anyString())).thenReturn(Mono.empty());
  }
}