package com.piter.bet.event.aggregator.streams;

import static com.piter.bet.event.aggregator.util.TestData.createBetRequestWithCorrectPrediction;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.util.kafka.MockProcessor;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.apache.commons.lang3.RandomUtils;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.logging.log4j.util.BiConsumer;
import org.junit.jupiter.api.Test;

class MatchTopologyConfigTest extends AbstractTopologyTest {

  private final MatchTopologyConfig matchTopologyConfig = new MatchTopologyConfig(1);

  @Override
  protected BiFunction<KStream<Long, Bet>, KStream<Long, Match>, KStream<Long, Bet>> getBetStreamFunction() {
    return matchTopologyConfig.matches();
  }

  @Test
  void shouldNotProcessBetBecauseMatchIsNotDeleteEvent() {
    //given
    var match = Match.builder().build();
    var bet = Bet.builder().build();
    var key = RandomUtils.nextLong();

    //when
    BiConsumer<TestInputTopic<Long, Bet>, TestInputTopic<Long, Match>> topicSender = (betTopic, matchTopic) -> {
      betTopic.pipeInput(key, bet);
      matchTopic.pipeInput(key, match);
    };

    //then
    Consumer<MockProcessor<Long, Bet, Void, Void>> asserterEmpty = MockProcessor::checkAndClearProcessedRecords;
    testAndAssertTopology(topicSender, asserterEmpty);
  }

  @Test
  void shouldCreateBetDeleteEventBecauseMatchIsDeleteEvent() {
    //given
    Match tombstoneMatch = null;
    var bet = createBetRequestWithCorrectPrediction();
    var key = RandomUtils.nextLong();
    Bet expectedTombstoneBet = null;

    //when
    BiConsumer<TestInputTopic<Long, Bet>, TestInputTopic<Long, Match>> topicSender = (betTopic, matchTopic) -> {
      betTopic.pipeInput(key, bet);
      matchTopic.pipeInput(key, tombstoneMatch);
    };

    //then
    Consumer<MockProcessor<Long, Bet, Void, Void>> asserter = processor -> processor.checkAndClearProcessedRecords(
        new Record<>(key, expectedTombstoneBet, 1L)
    );
    testAndAssertTopology(topicSender, asserter);
  }
}