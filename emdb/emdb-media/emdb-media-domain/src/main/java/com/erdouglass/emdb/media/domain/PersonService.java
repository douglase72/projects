package com.erdouglass.emdb.media.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.media.person.PersonResponse;
import com.erdouglass.emdb.media.person.SavePerson;

public interface PersonService {
  
  PersonResponse save(@NotNull @Valid SavePerson command);
}
