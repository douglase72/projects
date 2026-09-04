package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.CastOrder;
import com.erdouglass.emdb.media.kernel.Department;
import com.erdouglass.emdb.media.kernel.Name;
import com.erdouglass.emdb.media.kernel.Role;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.movie.application.port.in.CreditSpec.CastSpec;
import com.erdouglass.emdb.media.movie.application.port.in.CreditSpec.CrewSpec;

import lombok.Builder;

public sealed interface CreditSpec permits CastSpec, CrewSpec {
  
  TmdbCreditId tmdbId();
  TmdbId personId();
  Name name();
  
  @Builder
  public record CastSpec(
      TmdbCreditId tmdbId,
      TmdbId personId,
      Name name,
      Role character,
      CastOrder order) implements CreditSpec {

    public CastSpec {
      Objects.requireNonNull(tmdbId, "TMDB credit id is required");
      Objects.requireNonNull(personId, "TMDB person id is required");
      Objects.requireNonNull(name, "name is required");
    }
  }
  
  @Builder
  public record CrewSpec(
      TmdbCreditId tmdbId,
      TmdbId personId,
      Name name,
      Role job,
      Department department) implements CreditSpec {

    public CrewSpec {
      Objects.requireNonNull(tmdbId, "TMDB credit id is required");
      Objects.requireNonNull(personId, "TMDB person id is required");
      Objects.requireNonNull(name, "name is required");
    }
  } 
}
