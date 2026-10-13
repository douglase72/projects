package com.erdouglass.emdb.shared.kernel;

import org.jboss.logging.MDC;

public final class CorrelationScope implements AutoCloseable {
  public static final String MDC_KEY = "correlationId";

  private final Object previous;

  private CorrelationScope(Object previous) {
    this.previous = previous;
  }

  public static CorrelationScope open(CorrelationId id) {
    return new CorrelationScope(MDC.put(MDC_KEY, id.value().toString()));
  }

  @Override
  public void close() {
    if (previous == null) {
      MDC.remove(MDC_KEY);
    } else {
      MDC.put(MDC_KEY, previous);
    }
  }
}
