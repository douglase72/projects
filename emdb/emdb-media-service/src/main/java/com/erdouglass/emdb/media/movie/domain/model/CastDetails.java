package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.CastOrder;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Role;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.person.domain.model.Name;

import lombok.Builder;

@Builder
public record CastDetails(
    TmdbCreditId tmdbId, 
    PublicId personId, 
    Name name,
    Role character,
    CastOrder order) implements CreditDetails {

  public CastDetails {
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(personId, "personId is required");
    Objects.requireNonNull(name, "name is required");
    Objects.requireNonNull(order, "order is required");
  }
}
