package com.piter.bet.event.aggregator.prediction.match;

enum MathOperator {

  EQUAL() {
    @Override
    Boolean compare(int a, int b) {
      return a == b;
    }
  },
  NOT_EQUAL() {
    @Override
    Boolean compare(int a, int b) {
      return a != b;
    }
  };

  abstract Boolean compare(int a, int b);
}
