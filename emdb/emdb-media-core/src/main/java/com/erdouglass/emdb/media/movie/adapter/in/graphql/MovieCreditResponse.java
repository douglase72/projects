package com.erdouglass.emdb.media.movie.adapter.in.graphql;

import java.util.List;
import java.util.UUID;

import org.eclipse.microprofile.graphql.NonNull;

public record MovieCreditResponse(List<CastCredit> cast, List<CrewCredit> crew) {
  
  public record CastCredit(
      @NonNull UUID id,
      @NonNull Long personId,
      @NonNull String name,
      String character, 
      @NonNull Integer order) { }
  
  public record CrewCredit(
      @NonNull UUID id,
      @NonNull Long personId,
      @NonNull String name,
      String job, 
      String department) { }
}
