package com.erdouglass.emdb.scraper.domain.event;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.shared.kernel.CorrelationId;

public sealed interface MovieEvent permits MovieStarted, MovieExtracted {

  MessageId id();
  CorrelationId correlationId();
}
