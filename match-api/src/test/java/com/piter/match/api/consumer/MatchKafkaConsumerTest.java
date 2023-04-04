package com.piter.match.api.consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.piter.api.commons.domain.Match;
import com.piter.match.api.repository.MatchRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class MatchKafkaConsumerTest {

  @Mock
  private MatchRepository matchRepository;

  @Captor
  private ArgumentCaptor<Match> matchArgumentCaptor;

  @InjectMocks
  private MatchKafkaConsumer matchKafkaConsumer;

  @Test
  void shouldSaveMatch() {
    //given
    var match = Match.builder()
        .id(123L)
        .build();

    var matchMessage = MessageBuilder
        .withPayload(match)
        .build();

    mockSave(match);

    //when
    matchKafkaConsumer.matches().accept(matchMessage);

    //then
    verify(matchRepository).save(matchArgumentCaptor.capture());
    Match savedMatch = matchArgumentCaptor.getValue();
    assertThat(savedMatch).isEqualTo(match);
  }
  
  @Test
  void shouldDeleteMatch() {
    //given
    var messageKey = 123L;
    Message tombstoneMessage = org.springframework.integration.support.MessageBuilder
        .withPayload(KafkaNull.INSTANCE)
        .setHeader(KafkaHeaders.RECEIVED_MESSAGE_KEY, messageKey)
        .build();

    mockDelete();

    //when
    matchKafkaConsumer.matches().accept(tombstoneMessage);

    //then
    verify(matchRepository).deleteById(messageKey);
  }

  private void mockSave(Match match) {
    when(matchRepository.save(match)).thenReturn(Mono.just(match));
  }

  private void mockDelete() {
    when(matchRepository.deleteById(anyLong())).thenReturn(Mono.empty());
  }
}