package com.piter.bet.event.aggregator.streams;

import static com.piter.bet.event.aggregator.streams.SerdeUtils.BET_JSON_SERDE;
import static com.piter.bet.event.aggregator.streams.SerdeUtils.MATCH_JSON_SERDE;
import static com.piter.bet.event.aggregator.util.TestData.*;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.prediction.BetPredictionFetcher;
import com.piter.bet.event.aggregator.prediction.config.PredictionProperties;
import com.piter.bet.event.aggregator.prediction.expression.ExpressionPrediction;
import com.piter.bet.event.aggregator.service.BetServiceImpl;
import com.piter.bet.event.aggregator.util.YamlPropertySourceFactory;
import com.piter.bet.event.aggregator.util.kafka.MockProcessor;
import com.piter.bet.event.aggregator.util.kafka.MockProcessorSupplier;
import com.piter.bet.event.aggregator.util.TestData;
import com.piter.bet.event.aggregator.validation.BetValidator;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.TestInputTopic;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.logging.log4j.util.BiConsumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {PredictionProperties.class, LocalValidatorFactoryBean.class})
@EnableConfigurationProperties(value = PredictionProperties.class)
@PropertySource(value = "classpath:application-bet-prediction-rules-test.yaml", factory = YamlPropertySourceFactory.class)
class BetTopologyConfigTest {

  private static final String BET_REQUEST_TOPIC = "bet-request";
  private static final String MATCH_TOPIC = "match";

  @Autowired
  private Validator validator;

  @Autowired
  private PredictionProperties predictionProperties;

  private BetTopologyConfig betTopology;

  @BeforeEach
  void init() {
    List<ExpressionPrediction> expressionPredictions = predictionProperties.getRules();
    var betPredictionFetcher = new BetPredictionFetcher(expressionPredictions);
    var betService = new BetServiceImpl(betPredictionFetcher);
    var betValidator = new BetValidator(validator);
    var joiningTimeInHours = 168;
    betTopology = new BetTopologyConfig(joiningTimeInHours, betValidator, betService);
  }

  @ParameterizedTest
  @ValueSource(ints = {1, 5})
  void shouldProcessBetsWithoutResult(int numberOfBetRequests) {
    //given
    var match = createMatchWithoutResult();
    List<Bet> betRequests = Stream.generate(TestData::createBetRequestWithCorrectPrediction)
        .limit(numberOfBetRequests)
        .toList();

    var key = RandomStringUtils.randomAlphanumeric(3);
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
  void shouldProcessBetWithResult(Bet betRequestWithPrediction, Bet betWithResult) {
    //given
    var matchWithoutResult = betRequestWithPrediction.getMatch();
    var matchWithResult = createMatchWithResult();
    var key = RandomStringUtils.randomAlphanumeric(3);

    //when
    BiConsumer<TestInputTopic<String, Bet>, TestInputTopic<String, Match>> topicSender = (betTopic, matchTopic) -> {
      matchTopic.pipeInput(key, matchWithoutResult, 1L);
      betTopic.pipeInput(key, betRequestWithPrediction, 2L);
      matchTopic.pipeInput(key, matchWithResult, 3L);
    };

    //then
    Consumer<MockProcessor<String, Bet, Void, Void>> asserter = processor -> processor.checkAndClearProcessedRecords(
            new Record<>(key, betRequestWithPrediction, 4L),
            new Record<>(key, betWithResult, 5L)
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
    var key = RandomStringUtils.randomAlphanumeric(3);

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
    var key = RandomStringUtils.randomAlphanumeric(3);

    //when
    BiConsumer<TestInputTopic<String, Bet>, TestInputTopic<String, Match>> topicSender = (betTopic, matchTopic) -> {
      matchTopic.pipeInput(key, passedMatch);
      betTopic.pipeInput(key, betRequest);
    };

    //then
    Consumer<MockProcessor<String, Bet, Void, Void>> asserterEmpty = MockProcessor::checkAndClearProcessedRecords;
    testAndAssertTopology(topicSender, asserterEmpty);
  }

  private void testAndAssertTopology(
      BiConsumer<TestInputTopic<String, Bet>, TestInputTopic<String, Match>> topicSender,
      Consumer<MockProcessor<String, Bet, Void, Void>> mockProcessorAsserter) {

    final MockProcessorSupplier<String, Bet, Void, Void> supplier = new MockProcessorSupplier<>();

    var streamBuilder = new StreamsBuilder();

    Serde<String> stringSerde = Serdes.String();

    KStream<String, Bet> betRequestStream = streamBuilder.stream(BET_REQUEST_TOPIC,
        Consumed.with(stringSerde, BET_JSON_SERDE));
    KStream<String, Match> matchStream = streamBuilder.stream(MATCH_TOPIC,
        Consumed.with(stringSerde, MATCH_JSON_SERDE));

    KStream<String, Bet> betStream = betTopology.bets().apply(betRequestStream, matchStream);
    betStream.process(supplier);

    try (final var testDriver = new TopologyTestDriver(streamBuilder.build())) {
      TestInputTopic<String, Bet> inputBetTopic = testDriver.createInputTopic(BET_REQUEST_TOPIC,
          stringSerde.serializer(),
          BET_JSON_SERDE.serializer());

      TestInputTopic<String, Match> inputMatchTopic = testDriver.createInputTopic(MATCH_TOPIC,
          stringSerde.serializer(),
          MATCH_JSON_SERDE.serializer());

      final MockProcessor<String, Bet, Void, Void> processor = supplier.getProcessor();

      topicSender.accept(inputBetTopic, inputMatchTopic);

      mockProcessorAsserter.accept(processor);
    }
  }
}