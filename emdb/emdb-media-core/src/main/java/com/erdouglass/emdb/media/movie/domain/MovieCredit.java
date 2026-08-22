package com.erdouglass.emdb.media.movie.domain;

import java.util.Objects;

import com.erdouglass.emdb.media.TmdbId;
import com.erdouglass.emdb.media.person.domain.Name;
import com.erdouglass.emdb.media.person.domain.PersonId;

import lombok.Builder;

@Builder
public record MovieCredit(
    CreditId id,
    TmdbId tmdbId,
    MovieId movieId,
    PersonId personId,
    Name name,
    Role role,
    BillingOrder order) {

  public MovieCredit {
    Objects.requireNonNull(id, "credit id is required");
    Objects.requireNonNull(tmdbId, "tmdb id is required");
    Objects.requireNonNull(movieId, "movie id is required");
    Objects.requireNonNull(personId, "person id is required");
    Objects.requireNonNull(name, "name id is required");   
    Objects.requireNonNull(role, "role is required");
    Objects.requireNonNull(order, "billing order is required");
  }
}
