package com.erdouglass.emdb.media.movie.application.port.out;

import java.util.Map;
import java.util.Set;

import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.TmdbId;

public interface ResolvePersonStub {

  Map<TmdbId, PublicId> resolve(Set<PersonStub> stubs);
}
