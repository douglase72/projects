package com.erdouglass.emdb.media.kernel;

import java.util.Objects;

import com.erdouglass.emdb.media.kernel.Credit.CastDto;
import com.erdouglass.emdb.media.kernel.Credit.CrewDto;

import lombok.Builder;

public sealed interface Credit permits CastDto, CrewDto {
  
  TmdbCreditId tmdbId();
  AggregateId personId();
  Name name();
  
  @Builder
  public record CastDto(
      TmdbCreditId tmdbId,
      AggregateId personId,
      Name name,
      Role character,
      CastOrder order) implements Credit {

    public CastDto {
      Objects.requireNonNull(tmdbId, "TMDB credit id is required");
      Objects.requireNonNull(personId, "person id is required");
      Objects.requireNonNull(name, "name is required");
    }
  }
  
  @Builder
  public record CrewDto(
      TmdbCreditId tmdbId,
      AggregateId personId,
      Name name,
      Role job,
      Department department) implements Credit {

    public CrewDto {
      Objects.requireNonNull(tmdbId, "TMDB credit id is required");
      Objects.requireNonNull(personId, "person id is required");
      Objects.requireNonNull(name, "name is required");
    }
  } 
}
