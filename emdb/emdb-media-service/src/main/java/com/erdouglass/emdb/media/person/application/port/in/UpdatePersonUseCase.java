package com.erdouglass.emdb.media.person.application.port.in;

import com.erdouglass.emdb.media.kernel.UpdateResult;

public interface UpdatePersonUseCase {

  UpdateResult update(UpdatePersonCommand command);
}
