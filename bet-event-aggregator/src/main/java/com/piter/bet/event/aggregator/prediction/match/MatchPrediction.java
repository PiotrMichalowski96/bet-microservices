package com.piter.bet.event.aggregator.prediction.match;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.prediction.Prediction;
import lombok.Data;

@Data
public class MatchPrediction implements Prediction {

  private PredictionType firstCompare;
  private PredictionType secondCompare;
  private MathOperator mathOperator;

  @Override
  public boolean predict(Bet bet) {
    int firstCompareGoals = firstCompare.getGoals(bet);
    int secondCompareGoals = secondCompare.getGoals(bet);
    return mathOperator.compare(firstCompareGoals, secondCompareGoals);
  }
}
