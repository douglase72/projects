package com.erdouglass.emdb.media.movie.domain.model;

import com.erdouglass.emdb.media.kernel.AggregateId;
import com.erdouglass.emdb.media.kernel.Credit;
import com.erdouglass.emdb.media.kernel.CreditId;
import com.erdouglass.emdb.media.kernel.Name;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;

public sealed interface MovieCredit permits CastCredit, CrewCredit {

  CreditId id();
  TmdbCreditId tmdbId();
  AggregateId personId();
  Name name();
  void update(Credit details);
}
