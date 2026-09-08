package com.erdouglass.emdb.media.person.application.port.in;

import com.erdouglass.emdb.media.dto.SaveResult;

public interface SavePersonUseCase {

  SaveResult save(SavePersonCommand command);
}
