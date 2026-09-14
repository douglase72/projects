package com.erdouglass.emdb.media.person.application.port.in;

import java.util.Map;

import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.TmdbId;

public interface ResolvePersonUseCase {

  Map<TmdbId, PublicId> resolve(ResolvePersonCommand command);
}
