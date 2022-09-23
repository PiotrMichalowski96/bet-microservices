package com.piter.bet.event.aggregator.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class WaitUtil {

  public void waitMillis(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }
}
