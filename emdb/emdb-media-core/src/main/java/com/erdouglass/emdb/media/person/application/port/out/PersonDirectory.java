package com.erdouglass.emdb.media.person.application.port.out;

import java.util.Map;
import java.util.Set;

import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.person.domain.model.PersonPublicId;

public interface PersonDirectory {

  Map<TmdbId, PersonPublicId> register(Set<PersonStub> stubs);
}
