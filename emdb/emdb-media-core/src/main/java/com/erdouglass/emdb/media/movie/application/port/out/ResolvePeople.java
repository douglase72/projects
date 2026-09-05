package com.erdouglass.emdb.media.movie.application.port.out;

import java.util.Map;
import java.util.Set;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.AggregateId;

public interface ResolvePeople {

  Map<TmdbId, AggregateId> resolve(Set<PersonCredit> people);
}
