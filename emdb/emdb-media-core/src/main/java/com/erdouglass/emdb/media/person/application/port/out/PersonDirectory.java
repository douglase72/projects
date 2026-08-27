package com.erdouglass.emdb.media.person.application.port.out;

import java.util.Map;
import java.util.Set;

import com.erdouglass.emdb.media.kernel.SourceId;
import com.erdouglass.emdb.media.person.domain.PersonId;

public interface PersonDirectory {

  Map<SourceId, PersonId> resolve(Set<SourceId> sourceIds);
}
