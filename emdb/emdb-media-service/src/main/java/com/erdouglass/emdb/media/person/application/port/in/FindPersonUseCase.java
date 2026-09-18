package com.erdouglass.emdb.media.person.application.port.in;

import java.util.Optional;

import com.erdouglass.emdb.media.kernel.PublicId;

public interface FindPersonUseCase {
  
  Optional<PersonView> findById(PublicId id);
}
