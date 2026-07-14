package com.erdouglass.emdb.media;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface SavePersonUseCase {

  SaveResult save(@NotNull @Valid SavePerson command);
}
