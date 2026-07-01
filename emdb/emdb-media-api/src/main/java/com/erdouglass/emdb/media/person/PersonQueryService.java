package com.erdouglass.emdb.media.person;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.erdouglass.emdb.media.person.PersonDto.PersonCredits;

public interface PersonQueryService {
  
  PersonCredits findCreditsByPersonId(@NotNull @Positive Long id);

  PersonDto findById(@NotNull @Positive Long id);
}
