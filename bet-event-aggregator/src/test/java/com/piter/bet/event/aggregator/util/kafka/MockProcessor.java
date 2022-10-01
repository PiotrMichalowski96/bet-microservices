package com.piter.bet.event.aggregator.util.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;

/**
 * Modified test class from {link: https://github.com/apache/kafka/blob/trunk/streams/src/test/java/org/apache/kafka/test/MockApiProcessor.java}
 */
public class MockProcessor<KIn, VIn, KOut, VOut> implements Processor<KIn, VIn, KOut, VOut> {

  private final List<Record<KIn, VIn>> processed = new ArrayList<>();

  private ProcessorContext<KOut, VOut> context;

  @Override
  public void init(final ProcessorContext<KOut, VOut> context) {
    this.context = context;
  }

  @Override
  public void process(final Record<KIn, VIn> record) {
    processed.add(record);
    context.commit();
  }

  public void checkAndClearProcessedRecords(final Record<KIn, VIn>... expected) {
    assertThat(processed).hasSize(expected.length);
    for (int i = 0; i < expected.length; i++) {
      assertThat(processed.get(i).value()).usingRecursiveComparison()
              .ignoringFields("id")
              .isEqualTo(expected[i].value());
    }
    processed.clear();
  }

  public void checkAndClearProcessedRecords(List<Record<KIn, VIn>> expected) {
    Record<KIn, VIn>[] expectedArray = expected.toArray(new Record[0]);
    checkAndClearProcessedRecords(expectedArray);
  }
}