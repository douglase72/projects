package com.erdouglass.emdb.media.movie.domain.model;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.person.domain.model.Name;
import com.erdouglass.emdb.media.person.domain.model.PersonPublicId;

import lombok.Builder;

@Builder
public record CrewDetails(
    TmdbCreditId tmdbId, 
    PersonPublicId personId, 
    Name name,
    Role job,
    Department department) implements CreditDetails {

  public CrewDetails {
    Objects.requireNonNull(tmdbId, "TMDB credit id is required");
    Objects.requireNonNull(name, "name is required");
    Objects.requireNonNull(job, "job is required");
    Objects.requireNonNull(department, "department is required"); 
  }
}
