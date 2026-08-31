package com.erdouglass.emdb.media.movie.application.port.in;

import com.erdouglass.emdb.media.kernel.Name;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand.CastSpec;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand.CrewSpec;
import com.erdouglass.emdb.media.movie.domain.model.CreditDetails;
import com.erdouglass.emdb.media.person.domain.model.PersonPublicId;

public sealed interface CreditSpec permits CastSpec, CrewSpec {
  
  TmdbCreditId tmdbId();
  
  TmdbId personId();
  
  Name name();
  
  CreditDetails toDetails(PersonPublicId person);
}
