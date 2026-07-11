package com.erdouglass.emdb.media.application.port.inbound.person;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface PersonQueryService {
  
  PersonView findById(@NotNull @Positive Long id);
}
