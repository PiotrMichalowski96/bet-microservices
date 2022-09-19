package com.piter.bet.event.aggregator.streams;


import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.BetResults;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.domain.MatchRound;
import com.piter.bet.event.aggregator.domain.User;
import com.piter.bet.event.aggregator.util.MockProcessor;
import com.piter.bet.event.aggregator.util.MockProcessorSupplier;
import java.time.LocalDateTime;
import java.util.function.Consumer;
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
import org.springframework.kafka.support.serializer.JsonSerde;

class BetTopologyConfigTest {

  private static final String BET_REQUEST_TOPIC = "bet-request";
  private static final String MATCH_TOPIC = "match";

  private final BetTopologyConfig betTopology = new BetTopologyConfig();

  @Test
  void shouldCreateBet() {
    //given
    var match = Match.builder()
        .id(1L)
        .homeTeam("FC Barcelona")
        .awayTeam("Real Madrid")
        .startTime(LocalDateTime.of(2022, 2, 17, 21, 0, 0))
        .round(MatchRound.builder()
            .roundName("LaLiga round 30")
            .startTime(LocalDateTime.of(2022, 2, 14, 21, 0, 0))
            .build())
        .build();

    var bet = Bet.builder()
        .id(1L)
        .homeTeamGoalBet(1)
        .awayTeamGoalBet(1)
        .match(match)
        .user(User.builder().build())
        .betResults(BetResults.builder().build())
        .build();

    //when
    BiConsumer<TestInputTopic<String, Bet>, TestInputTopic<String, Match>> topicSender = (betTopic, matchTopic) -> {
      matchTopic.pipeInput("1", match);
      betTopic.pipeInput("1", bet);
    };

    //then
    Consumer<MockProcessor<String, Bet, Void, Void>> asserter =
        processor -> processor.checkAndClearProcessedRecords(new Record<>("1", bet, 1L));

    testTopology(topicSender, asserter);
  }

  private void testTopology(
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
}