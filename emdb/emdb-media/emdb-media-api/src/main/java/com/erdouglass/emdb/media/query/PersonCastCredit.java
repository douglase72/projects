package com.erdouglass.emdb.media.query;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

import io.smallrye.graphql.api.Union;

@Union
public sealed interface PersonCastCredit permits PersonMovieCastCredit, PersonSeriesCastCredit {
  @NotNull UUID creditId();
  @NotNull Long id();
  @NotNull String title();
  @NotNull Float score();
  String backdrop();
  String poster();
  String overview();
  @NotNull MediaType type();
}
