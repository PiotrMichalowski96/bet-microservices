package com.piter.bet.event.aggregator.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;

/**
 * Modified test class from {link: https://github.com/apache/kafka/blob/trunk/streams/src/test/java/org/apache/kafka/test/MockApiProcessor.java}
 */
public class MockProcessor<KIn, VIn, KOut, VOut> implements Processor<KIn, VIn, KOut, VOut> {

  private final ArrayList<Record<KIn, VIn>> processed = new ArrayList<>();

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

  public void checkAndClearProcessedRecords(final Record<?, ?>... expected) {
    assertThat("the number of outputs:" + processed, processed.size(), is(expected.length));
    for (int i = 0; i < expected.length; i++) {
      assertThat("key for output[" + i + "]:", processed.get(i).key(), is(expected[i].key()));
      assertThat("value for output[" + i + "]:", processed.get(i).value(), is(expected[i].value()));
    }
    processed.clear();
  }
}