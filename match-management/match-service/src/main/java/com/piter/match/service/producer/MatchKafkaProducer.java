package com.piter.match.service.producer;

import com.piter.match.service.domain.Match;
import com.piter.match.service.domain.MatchRound;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MatchKafkaProducer implements MatchProducer {

  @Value("${match.producer.binding}")
  private final String matchBinding;
  private final StreamBridge streamBridge;

  @Override
  public void sendMatch(Match match) {
    String kafkaKey = getRoundNameOrRandomText(match);

    Message<Match> matchMessage = MessageBuilder
        .withPayload(match)
        .setHeader(KafkaHeaders.MESSAGE_KEY, kafkaKey)
        .build();

    streamBridge.send(matchBinding, matchMessage);
    logger.debug("Sent match: {}", match);
  }

  private String getRoundNameOrRandomText(Match match) {
    return Optional.ofNullable(match.getRound())
        .map(MatchRound::getRoundName)
        .orElseThrow(() -> new IllegalArgumentException("There is missing round name in match: " + match));
  }
}
