package com.erdouglass.emdb.media.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.media.api.command.SavePerson;
import com.erdouglass.emdb.media.api.query.PersonResponse;

public interface PersonService {

  PersonResponse save(@NotNull @Valid SavePerson command);
}
