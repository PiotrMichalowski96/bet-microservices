package com.piter.bet.event.aggregator.prediction;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.prediction.expression.ExpressionPrediction;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BetPredictionFetcher {

  private final List<ExpressionPrediction> predictions;

  public int fetchPointsForPrediction(Bet bet) {
    return predictions.stream()
        .filter(prediction -> prediction.predict(bet))
        .findFirst()
        .map(ExpressionPrediction::getPoints)
        .orElse(0);
  }
}
