package com.erdouglass.emdb.ingest.application.port.out;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import lombok.Builder;

@Builder
public record Movie(
    Integer tmdbId,
    String title,
    String releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview,
    List<CastMember> cast,
    List<CrewMember> crew) implements Media {
  
  public Movie {
    Objects.requireNonNull(tmdbId, "tmdbId must not be null");
    Objects.requireNonNull(title, "title must not be null");
  } 
  
  public record CastMember(
      String tmdbCreditId, 
      Integer tmdbPersonId, 
      String name, 
      String character, 
      Integer order) { }
  
  public record CrewMember(
      String tmdbCreditId, 
      Integer tmdbPersonId, 
      String name, 
      String job, 
      String department) { }
}
