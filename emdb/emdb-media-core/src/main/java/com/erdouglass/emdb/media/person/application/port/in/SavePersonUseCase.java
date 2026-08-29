package com.erdouglass.emdb.media.person.application.port.in;

import com.erdouglass.emdb.media.kernel.Result;

public interface SavePersonUseCase {

  Result save(SavePersonCommand command);
}
