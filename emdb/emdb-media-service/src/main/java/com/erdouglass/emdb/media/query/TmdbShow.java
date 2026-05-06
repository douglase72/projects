package com.erdouglass.emdb.media.query;

public sealed interface TmdbShow permits TmdbMovie, TmdbSeries {

  Integer id();
  String backdrop_path();
  String poster_path();
  String homepage();
  String original_language();
  String tagline();
  String overview();
}
