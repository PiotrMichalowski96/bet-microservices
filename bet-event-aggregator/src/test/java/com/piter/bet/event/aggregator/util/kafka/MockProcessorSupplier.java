package com.piter.bet.event.aggregator.util.kafka;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;

/**
 * Simplified test class from {link: https://github.com/apache/kafka/blob/trunk/streams/src/test/java/org/apache/kafka/test/MockProcessorSupplier.java}
 */
public class MockProcessorSupplier<KIn, VIn, KOut, VOut> implements ProcessorSupplier<KIn, VIn, KOut, VOut> {

  private final List<MockProcessor<KIn, VIn, KOut, VOut>> processors = new ArrayList<>();

  @Override
  public Processor<KIn, VIn, KOut, VOut> get() {
    MockProcessor<KIn, VIn, KOut, VOut> processor = new MockProcessor<>();
    if (isNotCheckSupplierCall()) {
      processors.add(processor);
    }
    return processor;
  }

  private boolean isNotCheckSupplierCall() {
    return Arrays.stream(Thread.currentThread().getStackTrace())
        .noneMatch(caller -> "org.apache.kafka.streams.internals.ApiUtils".equals(caller.getClassName()) && "checkSupplier".equals(caller.getMethodName()));
  }

  public MockProcessor<KIn, VIn, KOut, VOut> getProcessor() {
    int lastElementIndex = processors.size() - 1;
    return processors.get(lastElementIndex);
  }
}