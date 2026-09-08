package com.erdouglass.emdb.media.person.application.port.out;

import java.util.Optional;

import com.erdouglass.emdb.media.kernel.PublicId;

public interface PersonQueryRepository {
  
  Optional<PersonView> findById(PublicId id);
}
