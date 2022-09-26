package com.piter.bet.event.aggregator.prediction.expression;

import static com.piter.bet.event.aggregator.exception.BetAggregatorException.cannotPredictException;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.prediction.Prediction;
import com.piter.bet.event.aggregator.prediction.match.MatchPrediction;
import java.util.List;
import lombok.Data;

@Data
public class ExpressionPrediction implements Prediction {

  private Integer points;
  private LogicalOperator logicalOperator;
  private List<MatchPrediction> subPredictions;

  @Override
  public boolean predict(Bet bet) {
    return subPredictions.stream()
        .map(prediction -> prediction.predict(bet))
        .reduce(logicalOperator.operator())
        .orElseThrow(() -> cannotPredictException(bet));
  }
}
