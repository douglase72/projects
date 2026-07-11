package com.erdouglass.emdb.media.application.port.inbound.person;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface PersonCommandService {

  PersonView update(@NotNull @Valid UpdatePerson command);
  
  void deleteById(@NotNull @Positive Long id);
}
