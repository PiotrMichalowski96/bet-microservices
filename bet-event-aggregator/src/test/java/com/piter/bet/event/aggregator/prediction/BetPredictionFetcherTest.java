package com.piter.bet.event.aggregator.prediction;

import static org.assertj.core.api.Assertions.assertThat;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.domain.MatchResult;
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
class BetPredictionFetcherTest {

  @Autowired
  private PredictionProperties predictionProperties;

  private BetPredictionFetcher betPredictionFetcher;

  @BeforeEach
  void init() {
    List<ExpressionPrediction> expressionPredictions = predictionProperties.getRules();
    betPredictionFetcher = new BetPredictionFetcher(expressionPredictions);
  }

  @ParameterizedTest
  @MethodSource("predictionsAndPoints")
  void shouldReturnPointsBasedOnMatchPrediction(int homeTeamGoalsBet,
      int awayTeamGoalsBet,
      MatchResult matchResult,
      int expectedPoints) {

    //given
    Bet bet = createBetWithResults(homeTeamGoalsBet, awayTeamGoalsBet, matchResult);

    //when
    int actualPoints = betPredictionFetcher.fetchPointsForPrediction(bet);

    //then
    assertThat(actualPoints).isEqualTo(expectedPoints);
  }

  private static Stream<Arguments> predictionsAndPoints() {
    return Stream.of(
        // Correct result prediction
        Arguments.of(2, 1,
            MatchResult.builder()
                .homeTeamGoals(2)
                .awayTeamGoals(1)
                .build(),
            5),
        // Draw prediction
        Arguments.of(1, 1,
            MatchResult.builder()
                .homeTeamGoals(2)
                .awayTeamGoals(2)
                .build(),
            3),
        // Correct one team goals prediction
        Arguments.of(4, 1,
            MatchResult.builder()
                .homeTeamGoals(2)
                .awayTeamGoals(1)
                .build(),
            3),
        // No correct goals prediction
        Arguments.of(4, 1,
            MatchResult.builder()
                .homeTeamGoals(1)
                .awayTeamGoals(0)
                .build(),
            1)
    );
  }

  private Bet createBetWithResults(
      int homeTeamGoalBet,
      int awayTeamGoalBet,
      MatchResult matchResult) {

    Match match = Match.builder()
        .result(matchResult)
        .build();

    return Bet.builder()
        .homeTeamGoalBet(homeTeamGoalBet)
        .awayTeamGoalBet(awayTeamGoalBet)
        .match(match)
        .build();
  }
}