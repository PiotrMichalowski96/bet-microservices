package com.piter.bet.event.aggregator.streams;

import static com.piter.bet.event.aggregator.streams.SerdeUtils.BET_JSON_SERDE;
import static com.piter.bet.event.aggregator.streams.SerdeUtils.MATCH_JSON_SERDE;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.util.kafka.MockProcessor;
import com.piter.bet.event.aggregator.util.kafka.MockProcessorSupplier;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.logging.log4j.util.BiConsumer;

abstract class AbstractTopologyTest {

  private static final String BET_REQUEST_TOPIC = "bet-request";
  private static final String MATCH_TOPIC = "match";

  protected abstract BiFunction<KStream<Long, Bet>, KStream<Long, Match>, KStream<Long, Bet>> getBetStreamFunction();

  protected void testAndAssertTopology(
      BiConsumer<TestInputTopic<Long, Bet>, TestInputTopic<Long, Match>> topicSender,
      Consumer<MockProcessor<Long, Bet, Void, Void>> mockProcessorAsserter) {

    final MockProcessorSupplier<Long, Bet, Void, Void> supplier = new MockProcessorSupplier<>();

    var streamBuilder = new StreamsBuilder();

    Serde<Long> longSerde = Serdes.Long();

    KStream<Long, Bet> betRequestStream = streamBuilder.stream(BET_REQUEST_TOPIC,
        Consumed.with(longSerde, BET_JSON_SERDE));
    KStream<Long, Match> matchStream = streamBuilder.stream(MATCH_TOPIC,
        Consumed.with(longSerde, MATCH_JSON_SERDE));

    KStream<Long, Bet> betStream = getBetStreamFunction().apply(betRequestStream, matchStream);
    betStream.process(supplier);

    try (final var testDriver = new TopologyTestDriver(streamBuilder.build())) {
      TestInputTopic<Long, Bet> inputBetTopic = testDriver.createInputTopic(BET_REQUEST_TOPIC,
          longSerde.serializer(),
          BET_JSON_SERDE.serializer());

      TestInputTopic<Long, Match> inputMatchTopic = testDriver.createInputTopic(MATCH_TOPIC,
          longSerde.serializer(),
          MATCH_JSON_SERDE.serializer());

      final MockProcessor<Long, Bet, Void, Void> processor = supplier.getProcessor();

      topicSender.accept(inputBetTopic, inputMatchTopic);

      mockProcessorAsserter.accept(processor);
    }
  }
}
