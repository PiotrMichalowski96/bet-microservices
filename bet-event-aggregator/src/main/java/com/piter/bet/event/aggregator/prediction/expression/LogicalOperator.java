package com.piter.bet.event.aggregator.prediction.expression;

import java.util.function.BinaryOperator;

public enum LogicalOperator {

  AND() {
    @Override
    public BinaryOperator<Boolean> operator() {
      return (a, b) -> a && b;
    }
  },
  OR() {
    @Override
    public BinaryOperator<Boolean> operator() {
      return (a, b) -> a || b;
    }
  };

  public abstract BinaryOperator<Boolean> operator();
}
