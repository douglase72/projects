package com.erdouglass.emdb.media.movie.domain.event;

import com.erdouglass.common.messaging.MessageId;
import com.erdouglass.emdb.media.shared.domain.model.MediaId;
import com.erdouglass.emdb.shared.kernel.CorrelationId;
import com.erdouglass.emdb.shared.kernel.TmdbId;

public sealed interface MovieEvent permits MovieSaved {
  
  MessageId id();
  CorrelationId correlationId();
  MediaId mediaId();
  TmdbId tmdbId();
}
