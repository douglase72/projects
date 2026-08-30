package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.Name;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.person.domain.model.PersonPublicId;

import lombok.Builder;

@Builder
public record CastDetails(
    TmdbCreditId tmdbId, 
    PersonPublicId personId, 
    Name name,
    Role character,
    CastOrder order) implements CreditDetails {
  
  public CastDetails {
    Objects.requireNonNull(tmdbId, "TMDB credit id is required");
    Objects.requireNonNull(personId, "TMDB person id is required");
    Objects.requireNonNull(name, "name is required");
  }
  
  CastDetails withOrder(CastOrder order) {
    Objects.requireNonNull(order, "order is required");
    return new CastDetails(tmdbId, personId, name, character, order);
  }  
}
