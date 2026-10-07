package com.erdouglass.emdb.media.movie.application.port.out;

import com.erdouglass.emdb.media.messaging.MediaEvent;

public interface MediaEventPublisher {

  void publish(MediaEvent event);
}
