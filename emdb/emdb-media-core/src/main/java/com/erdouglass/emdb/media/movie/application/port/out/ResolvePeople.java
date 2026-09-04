package com.erdouglass.emdb.media.movie.application.port.out;

import java.util.Map;
import java.util.Set;

import com.erdouglass.emdb.media.kernel.AggregateId;
import com.erdouglass.emdb.media.kernel.TmdbId;

public interface ResolvePeople {

  Map<TmdbId, AggregateId> resolve(Set<PersonCredit> people);
}
