package com.erdouglass.emdb.media.movie.domain.model;

import com.erdouglass.emdb.media.kernel.Name;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.person.domain.model.PersonPublicId;

public sealed interface CreditDetails permits CastDetails, CrewDetails {
  TmdbCreditId tmdbId();
  PersonPublicId personId();
  Name name();
}
