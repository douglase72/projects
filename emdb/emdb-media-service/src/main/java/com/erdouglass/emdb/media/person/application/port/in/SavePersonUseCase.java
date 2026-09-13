package com.erdouglass.emdb.media.person.application.port.in;

import com.erdouglass.emdb.media.SavePersonCommand;

public interface SavePersonUseCase {

  void save(SavePersonCommand command);
}
