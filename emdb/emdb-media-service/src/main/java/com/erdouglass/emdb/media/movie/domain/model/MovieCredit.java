package com.erdouglass.emdb.media.movie.domain.model;

import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.person.domain.model.Name;

public sealed interface MovieCredit permits CastCredit, CrewCredit {
  PublicId id();
  TmdbCreditId tmdbId();
  PublicId personId();
  Name name();
  CreditDetails details();
  void update(CreditDetails details);
}
