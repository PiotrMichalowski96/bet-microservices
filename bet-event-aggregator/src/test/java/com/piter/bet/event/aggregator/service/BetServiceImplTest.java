package com.piter.bet.event.aggregator.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.BetResults;
import com.piter.bet.event.aggregator.domain.BetResults.Status;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.domain.MatchResult;
import com.piter.bet.event.aggregator.prediction.BetPredictionFetcher;
import com.piter.bet.event.aggregator.prediction.config.PredictionProperties;
import com.piter.bet.event.aggregator.prediction.expression.ExpressionPrediction;
import com.piter.bet.event.aggregator.util.YamlPropertySourceFactory;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = PredictionProperties.class)
@EnableConfigurationProperties(value = PredictionProperties.class)
@PropertySource(value = "classpath:application-bet-prediction-rules-test.yaml", factory = YamlPropertySourceFactory.class)
class BetServiceImplTest {

  @Autowired
  private PredictionProperties predictionProperties;

  private BetService betService;

  @BeforeEach
  void init() {
    List<ExpressionPrediction> expressionPredictions = predictionProperties.getRules();
    BetPredictionFetcher betPredictionFetcher = new BetPredictionFetcher(expressionPredictions);
    betService = new BetServiceImpl(betPredictionFetcher);
  }

  @ParameterizedTest
  @MethodSource("predictionsAndPoints")
  void pointsBasedOnMatchPrediction(MatchResult predictedMatchResult,
      MatchResult actualMatchResult,
      BetResults expectedBetResult) {

    //given
    Bet bet = createBetWithResults(predictedMatchResult, actualMatchResult);

    //when
    Bet actualBet = betService.fetchBetResult(bet);
    BetResults actualBetResult = actualBet.getBetResults();

    //then
    assertThat(actualBetResult).isEqualTo(expectedBetResult);
  }

  private static Stream<Arguments> predictionsAndPoints() {
    return Stream.of(
        // Correct result prediction
        Arguments.of(
            MatchResult.builder()
                .homeTeamGoals(2)
                .awayTeamGoals(1)
                .build(),
            MatchResult.builder()
                .homeTeamGoals(2)
                .awayTeamGoals(1)
                .build(),
            BetResults.builder()
                .status(Status.CORRECT)
                .points(5)
                .build()),
        // Draw prediction
        Arguments.of(
            MatchResult.builder()
                .homeTeamGoals(1)
                .awayTeamGoals(1)
                .build(),
            MatchResult.builder()
                .homeTeamGoals(2)
                .awayTeamGoals(2)
                .build(),
            BetResults.builder()
                .status(Status.CORRECT)
                .points(3)
                .build()),
        // Correct one team goals prediction
        Arguments.of(
            MatchResult.builder()
                .homeTeamGoals(4)
                .awayTeamGoals(1)
                .build(),
            MatchResult.builder()
                .homeTeamGoals(2)
                .awayTeamGoals(1)
                .build(),
            BetResults.builder()
                .status(Status.CORRECT)
                .points(3)
                .build()),
        // No correct goals prediction
        Arguments.of(
            MatchResult.builder()
                .homeTeamGoals(4)
                .awayTeamGoals(1)
                .build(),
            MatchResult.builder()
                .homeTeamGoals(1)
                .awayTeamGoals(0)
                .build(),
            BetResults.builder()
                .status(Status.CORRECT)
                .points(1)
                .build()),
        // Wrong match result prediction
        Arguments.of(
            MatchResult.builder()
                .homeTeamGoals(2)
                .awayTeamGoals(1)
                .build(),
            MatchResult.builder()
                .homeTeamGoals(2)
                .awayTeamGoals(3)
                .build(),
            BetResults.builder()
                .status(Status.INCORRECT)
                .points(0)
                .build()),
        // Wrong match result prediction
        Arguments.of(
            MatchResult.builder()
                .homeTeamGoals(1)
                .awayTeamGoals(1)
                .build(),
            MatchResult.builder()
                .homeTeamGoals(2)
                .awayTeamGoals(3)
                .build(),
            BetResults.builder()
                .status(Status.INCORRECT)
                .points(0)
                .build())
    );
  }

  private Bet createBetWithResults(MatchResult predictedMatchResult, MatchResult actualMatchResult) {

    Match match = Match.builder()
        .result(actualMatchResult)
        .build();

    return Bet.builder()
        .matchPredictedResult(predictedMatchResult)
        .match(match)
        .build();
  }

}