package com.erdouglass.emdb.media.person.application.port.in;

import com.erdouglass.emdb.media.SavePersonCommand;
import com.erdouglass.emdb.media.kernel.SaveResult;

public interface SavePersonUseCase {

  SaveResult save(SavePersonCommand command);
}
