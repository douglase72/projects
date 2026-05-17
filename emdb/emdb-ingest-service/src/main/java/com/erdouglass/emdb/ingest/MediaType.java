package com.erdouglass.emdb.ingest;

import com.fasterxml.jackson.annotation.JsonValue;

/// The kind of media being ingested.
///
/// Each constant has a lowercase JSON form (`"movie"`, `"person"`,
/// `"series"`) that matches the TMDB API and what callers send on the
/// `POST /ingest` endpoint. The mapping is applied by Jackson via
/// [#toString] / [JsonValue].
public enum MediaType {
  MOVIE("movie"), 
  PERSON("person"),
  SERIES("series");
  
  private final String type;
  
  MediaType(String type) {
    this.type = type;
  }
  
  @Override
  @JsonValue
  public String toString() {
    return type;
  }
}
