package com.piter.bet.event.aggregator.prediction.expression;

import java.util.function.BinaryOperator;

enum LogicalOperator {

  AND() {
    @Override
    BinaryOperator<Boolean> operator() {
      return (a, b) -> a && b;
    }
  },
  OR() {
    @Override
    BinaryOperator<Boolean> operator() {
      return (a, b) -> a || b;
    }
  };

  abstract BinaryOperator<Boolean> operator();
}
