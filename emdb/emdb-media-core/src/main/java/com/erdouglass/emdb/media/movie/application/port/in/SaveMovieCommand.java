package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.List;
import java.util.Objects;

import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.movie.domain.model.CastDetails;
import com.erdouglass.emdb.media.movie.domain.model.CastOrder;
import com.erdouglass.emdb.media.movie.domain.model.CreditDetails;
import com.erdouglass.emdb.media.movie.domain.model.CrewDetails;
import com.erdouglass.emdb.media.movie.domain.model.Department;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.model.Role;
import com.erdouglass.emdb.media.person.domain.model.Name;
import com.erdouglass.emdb.media.person.domain.model.PersonPublicId;

import lombok.Builder;

public record SaveMovieCommand(
    TmdbId tmdbId,
    MovieDetails details,
    List<CreditSpec> credits) {
  
  public SaveMovieCommand {
    Objects.requireNonNull(tmdbId, "TMDB id is required");
    Objects.requireNonNull(details, "movie details are required");
  }
  
  public static SaveMovieCommand of(TmdbId tmdbId, MovieDetails details, List<CreditSpec> credits) {
    return new SaveMovieCommand(tmdbId, details, credits);
  }
  
  @Builder
  public record CastSpec(
      TmdbCreditId tmdbId, TmdbId personId, Name name, Role character, CastOrder order) implements CreditSpec {    
    
    public CastSpec {
      Objects.requireNonNull(tmdbId, "TMDB credit id is required");
      Objects.requireNonNull(personId, "TMDB person id is required");
      Objects.requireNonNull(name, "name is required");
    }

    @Override
    public CreditDetails toDetails(PersonPublicId person) {
      return new CastDetails(tmdbId, person, name, character, order);
    }
  }
  
  @Builder
  public record CrewSpec(
      TmdbCreditId tmdbId, TmdbId personId, Name name, Role job, Department department) implements CreditSpec { 
    
    public CrewSpec {
      Objects.requireNonNull(tmdbId, "TMDB credit id is required");
      Objects.requireNonNull(personId, "TMDB person id is required");
      Objects.requireNonNull(name, "name is required");
    }

    @Override
    public CreditDetails toDetails(PersonPublicId person) {
      return new CrewDetails(tmdbId, person, name, job, department);
    }
  }
}
