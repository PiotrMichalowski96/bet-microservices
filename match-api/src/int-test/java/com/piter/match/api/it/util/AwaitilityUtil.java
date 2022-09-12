package com.piter.match.api.it.util;

import static org.awaitility.Awaitility.given;

import java.time.Duration;
import lombok.experimental.UtilityClass;
import org.awaitility.core.ThrowingRunnable;

@UtilityClass
public class AwaitilityUtil {

  private static final Duration TEST_TIMEOUT = Duration.ofSeconds(30);
  private static final Duration POLL_INTERVAL = Duration.ofSeconds(2L);

  public static void assertAsync(final ThrowingRunnable assertion) {
    given().ignoreExceptions()
        .await()
        .atMost(TEST_TIMEOUT)
        .pollInterval(POLL_INTERVAL)
        .untilAsserted(assertion);
  }
}
