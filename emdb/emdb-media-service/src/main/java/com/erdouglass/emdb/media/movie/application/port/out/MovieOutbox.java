package com.erdouglass.emdb.media.movie.application.port.out;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.media.movie.domain.event.MovieEvent;

public interface MovieOutbox {

  boolean hasProcessed(MessageId id);
  void markProcessed(MovieEvent event);
}
