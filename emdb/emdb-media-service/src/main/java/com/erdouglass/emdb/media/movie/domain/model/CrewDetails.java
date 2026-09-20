package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.Department;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Role;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.person.domain.model.Name;

import lombok.Builder;

@Builder
public record CrewDetails(
    TmdbCreditId tmdbId, 
    PublicId personId, 
    Name name,
    Role job,
    Department department) implements CreditDetails {

  public CrewDetails {
    Objects.requireNonNull(tmdbId, "tmdbId is required");
    Objects.requireNonNull(personId, "personId is required");
    Objects.requireNonNull(name, "name is required");
  }
}
