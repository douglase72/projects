package com.erdouglass.emdb.media.person.application.port.in;

public interface SavePersonUseCase {
  
  Result save(SavePersonCommand command);
}
