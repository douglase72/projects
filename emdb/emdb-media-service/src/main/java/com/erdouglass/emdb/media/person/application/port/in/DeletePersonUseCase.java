package com.erdouglass.emdb.media.person.application.port.in;

import com.erdouglass.emdb.media.kernel.PublicId;

public interface DeletePersonUseCase {

  void deleteById(PublicId id);
}
