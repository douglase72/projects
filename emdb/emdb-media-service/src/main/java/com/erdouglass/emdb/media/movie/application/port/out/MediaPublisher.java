package com.erdouglass.emdb.media.movie.application.port.out;

import com.erdouglass.common.messaging.Correlation;
import com.erdouglass.emdb.media.messaging.MediaSavedMessage;

public interface MediaPublisher {

  void publish(MediaSavedMessage event, Correlation correlation);
}
