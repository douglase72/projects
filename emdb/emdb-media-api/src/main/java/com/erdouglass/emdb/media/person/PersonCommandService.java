package com.erdouglass.emdb.media.person;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface PersonCommandService {

  /// Save the person in the given command to the database.
  /// 
  /// This operation is idempotent with respect to the persons TMDB id, no 
  /// matter how many times the operation is invoked the persons TMDB id will
  /// never change.
  PersonDto save(@NotNull @Valid SavePerson command);
  
  PersonDto update(@NotNull @Valid UpdatePerson command);
  
  void delete(@NotNull @Positive Long id);
}
