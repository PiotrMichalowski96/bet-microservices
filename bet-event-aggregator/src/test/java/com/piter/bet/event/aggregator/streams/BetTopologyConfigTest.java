package com.piter.bet.event.aggregator.streams;

import static com.piter.bet.event.aggregator.util.TestData.*;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.util.MockProcessor;
import com.piter.bet.event.aggregator.util.MockProcessorSupplier;
import com.piter.bet.event.aggregator.util.TestData;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.logging.log4j.util.BiConsumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.kafka.support.serializer.JsonSerde;

class BetTopologyConfigTest {

  private static final String BET_REQUEST_TOPIC = "bet-request";
  private static final String MATCH_TOPIC = "match";

  private final BetTopologyConfig betTopology = new BetTopologyConfig();

  @ParameterizedTest
  @ValueSource(ints = {1, 5})
  void shouldProcessBetsWithoutResult(int numberOfBetRequests) {
    //given
    var match = createMatchWithoutResult();
    List<Bet> betRequests = Stream.generate(TestData::createBetRequestWithCorrectPrediction)
        .limit(numberOfBetRequests)
        .toList();

    var key = "1";
    List<Record<String, Bet>> expectedRecords = Stream.generate(TestData::createBetWithoutResult)
        .limit(numberOfBetRequests)
        .map(betResult -> new Record<>(key, betResult, 1L))
        .toList();

    //when
    BiConsumer<TestInputTopic<String, Bet>, TestInputTopic<String, Match>> topicSender = (betTopic, matchTopic) -> {
      matchTopic.pipeInput(key, match);
      betRequests.forEach(bet -> betTopic.pipeInput(key, bet));
    };

    //then
    Consumer<MockProcessor<String, Bet, Void, Void>> asserter =
        processor -> processor.checkAndClearProcessedRecords(expectedRecords);

    testAndAssertTopology(topicSender, asserter);
  }

  @ParameterizedTest
  @MethodSource("provideBetRequestAndBetResult")
  void shouldProcessBetWithCorrectResult(Bet betRequestWithPrediction, Bet betWithResult) {
    //given
    var matchWithoutResult = createMatchWithoutResult();
    var matchWithResult = createMatchWithResult();

    var key = "1";
    var betWithoutResult = createBetWithoutResult();

    //when
    BiConsumer<TestInputTopic<String, Bet>, TestInputTopic<String, Match>> topicSender = (betTopic, matchTopic) -> {
      matchTopic.pipeInput(key, matchWithoutResult);
      betTopic.pipeInput(key, betRequestWithPrediction);
      matchTopic.pipeInput(key, matchWithResult);
    };

    //then
    Consumer<MockProcessor<String, Bet, Void, Void>> asserter = processor -> processor.checkAndClearProcessedRecords(
            new Record<>(key, betWithoutResult, 1L),
            new Record<>(key, betWithResult, 1L)
    );
    testAndAssertTopology(topicSender, asserter);
  }

  private static Stream<Arguments> provideBetRequestAndBetResult() {
    return Stream.of(
        Arguments.of(createBetRequestWithCorrectPrediction(), createBetWithCorrectResult()),
        Arguments.of(createBetRequestWithWrongPrediction(), createBetWithIncorrectResult())
    );
  }

  @Test
  void shouldNotProcessBetWhenMatchHasAlreadyResult() {
    //given
    var matchWithResult = createMatchWithResult();
    var betRequest = createBetRequestWithCorrectPrediction();
    var key = "1";

    //when
    BiConsumer<TestInputTopic<String, Bet>, TestInputTopic<String, Match>> topicSender = (betTopic, matchTopic) -> {
      matchTopic.pipeInput(key, matchWithResult);
      betTopic.pipeInput(key, betRequest);
    };

    //then
    Consumer<MockProcessor<String, Bet, Void, Void>> asserterEmpty = MockProcessor::checkAndClearProcessedRecords;
    testAndAssertTopology(topicSender, asserterEmpty);
  }

  @Test
  void shouldProcessMultipleBetsAndMatches() {
    //given
    var firstMatch = createMatchWithoutResult();
    var secondMatch = createSecondMatchWithoutResult();

    var firstBetRequest = createBetRequestWithCorrectPrediction();
    var secondBetRequest = createSecondBetRequest();

    var firstBetResult = createBetWithoutResult();
    var secondBetResult = createSecondBetWithoutResult();

    var firstKey = "1";
    var secondKey = "2";

    //when
    BiConsumer<TestInputTopic<String, Bet>, TestInputTopic<String, Match>> topicSender = (betTopic, matchTopic) -> {
      matchTopic.pipeInput(firstKey, firstMatch);
      matchTopic.pipeInput(secondKey, secondMatch);
      betTopic.pipeInput(firstKey, firstBetRequest);
      betTopic.pipeInput(secondKey, secondBetRequest);
    };

    //then
    Consumer<MockProcessor<String, Bet, Void, Void>> asserter = processor -> processor.checkAndClearProcessedRecords(
        new Record<>(firstKey, firstBetResult, 1L),
        new Record<>(secondKey, secondBetResult, 1L)
    );
    testAndAssertTopology(topicSender, asserter);
  }

  @Test
  void shouldNotProcessInvalidBetBecauseOfWrongMatchInBet() {
    //given
    var match = createMatchWithoutResult();
    var invalidBetWithWrongMatch = createSecondBetRequest();
    var key = "1";

    //when
    BiConsumer<TestInputTopic<String, Bet>, TestInputTopic<String, Match>> topicSender = (betTopic, matchTopic) -> {
      matchTopic.pipeInput(key, match);
      betTopic.pipeInput(key, invalidBetWithWrongMatch);
    };

    //then
    Consumer<MockProcessor<String, Bet, Void, Void>> asserterEmpty = MockProcessor::checkAndClearProcessedRecords;
    testAndAssertTopology(topicSender, asserterEmpty);
  }

  @Test
  void shouldNotProcessBetBecauseMatchAlreadyPassed() {
    //given
    var passedMatch = createMatchWithTime(LocalDateTime.MIN);
    var betRequest = createBetRequestWithCorrectPrediction();
    var key = "1";

    //when
    BiConsumer<TestInputTopic<String, Bet>, TestInputTopic<String, Match>> topicSender = (betTopic, matchTopic) -> {
      matchTopic.pipeInput(key, passedMatch);
      betTopic.pipeInput(key, betRequest);
    };

    //then
    Consumer<MockProcessor<String, Bet, Void, Void>> asserterEmpty = MockProcessor::checkAndClearProcessedRecords;
    testAndAssertTopology(topicSender, asserterEmpty);
  }

  @Test
  void shouldProcessOneBetAndRejectSecondBecauseMatchAlreadyStarted() {
    //given
    var betRequest = createBetRequestWithCorrectPrediction();
    var key = "1";
    var bet = createBetWithoutResult();

    //when
    BiConsumer<TestInputTopic<String, Bet>, TestInputTopic<String, Match>> topicSender = (betTopic, matchTopic) -> {
      var startTime = LocalDateTime.now().plusSeconds(5L);
      var match = createMatchWithTime(startTime);
      matchTopic.pipeInput(key, match);
      betTopic.pipeInput(key, betRequest);
      waitMillis(10L);
      betTopic.pipeInput(key, betRequest);
    };

    //then
    Consumer<MockProcessor<String, Bet, Void, Void>> asserter = processor -> processor.checkAndClearProcessedRecords(
        new Record<>(key, bet, 1L)
    );
    testAndAssertTopology(topicSender, asserter);
  }

  @Test
  void shouldProcessWithResultOneBetAndRejectSecondBecauseMatchAlreadyStarted() {
    //given
    var matchWithResult = createMatchWithResult();
    var betRequestWithCorrectPrediction = createBetRequestWithCorrectPrediction();
    var key = "1";
    var betWithoutResult = createBetWithoutResult();
    var betWithCorrectResult = createBetWithCorrectResult();

    //when
    BiConsumer<TestInputTopic<String, Bet>, TestInputTopic<String, Match>> topicSender = (betTopic, matchTopic) -> {
      var startTime = LocalDateTime.now().plusSeconds(5L);
      var match = createMatchWithTime(startTime);
      matchTopic.pipeInput(key, match);
      betTopic.pipeInput(key, betRequestWithCorrectPrediction);
      waitMillis(10L);
      betTopic.pipeInput(key, betRequestWithCorrectPrediction);
      matchTopic.pipeInput(key, matchWithResult);
    };

    //then
    Consumer<MockProcessor<String, Bet, Void, Void>> asserter = processor -> processor.checkAndClearProcessedRecords(
        new Record<>(key, betWithoutResult, 1L),
        new Record<>(key, betWithCorrectResult, 1L)
    );
    testAndAssertTopology(topicSender, asserter);
  }

  private void testAndAssertTopology(
      BiConsumer<TestInputTopic<String, Bet>, TestInputTopic<String, Match>> topicSender,
      Consumer<MockProcessor<String, Bet, Void, Void>> mockProcessorAsserter) {

    final MockProcessorSupplier<String, Bet, Void, Void> supplier = new MockProcessorSupplier<>();

    var streamBuilder = new StreamsBuilder();

    Serde<String> stringSerde = Serdes.String();
    JsonSerde<Bet> betJsonSerde = new JsonSerde<>(Bet.class);
    JsonSerde<Match> matchJsonSerde = new JsonSerde<>(Match.class);

    KStream<String, Bet> betRequestStream = streamBuilder.stream(BET_REQUEST_TOPIC,
        Consumed.with(stringSerde, betJsonSerde));
    KTable<String, Match> matchStream = streamBuilder.stream(MATCH_TOPIC,
        Consumed.with(stringSerde, matchJsonSerde)).toTable();

    KStream<String, Bet> betStream = betTopology.bets().apply(betRequestStream, matchStream);
    betStream.process(supplier);

    try (final var testDriver = new TopologyTestDriver(streamBuilder.build())) {
      TestInputTopic<String, Bet> inputBetTopic = testDriver.createInputTopic(BET_REQUEST_TOPIC,
          stringSerde.serializer(),
          betJsonSerde.serializer());

      TestInputTopic<String, Match> inputMatchTopic = testDriver.createInputTopic(MATCH_TOPIC,
          stringSerde.serializer(),
          matchJsonSerde.serializer());

      final MockProcessor<String, Bet, Void, Void> processor = supplier.getProcessor();

      topicSender.accept(inputBetTopic, inputMatchTopic);

      mockProcessorAsserter.accept(processor);
    }
  }

  @SneakyThrows
  private void waitMillis(long millis) {
    Thread.sleep(millis);
  }
}