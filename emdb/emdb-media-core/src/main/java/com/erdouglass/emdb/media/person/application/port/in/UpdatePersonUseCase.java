package com.erdouglass.emdb.media.person.application.port.in;

import com.erdouglass.emdb.media.dto.UpdateResult;

public interface UpdatePersonUseCase {

  UpdateResult update(UpdatePersonCommand command);
}
