package com.piter.bet.event.aggregator.prediction.match;


public enum MathOperator {

  EQUAL() {
    @Override
    public Boolean compare(int a, int b) {
      return a == b;
    }
  },
  NOT_EQUAL() {
    @Override
    public Boolean compare(int a, int b) {
      return a != b;
    }
  };

  public abstract Boolean compare(int a, int b);
}
