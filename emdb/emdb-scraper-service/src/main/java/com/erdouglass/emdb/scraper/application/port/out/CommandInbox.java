package com.erdouglass.emdb.scraper.application.port.out;

import com.erdouglass.common.messaging.MessageId;

public interface CommandInbox {

  boolean hasProcessed(MessageId id);
  
  void markProcessed(MessageId id);
}
