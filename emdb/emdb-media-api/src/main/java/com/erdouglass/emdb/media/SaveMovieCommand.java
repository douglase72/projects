package com.erdouglass.emdb.media;

import java.math.BigDecimal;
import java.util.List;

/// Published contract between the Ingest service and the Media service.
public record SaveMovieCommand(
    Integer tmdbId,
    String title,
    String releaseDate,
    BigDecimal score,
    String originalLanguage,
    String overview,
    List<CastMember> cast,
    List<CrewMember> crew) { 
  
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
