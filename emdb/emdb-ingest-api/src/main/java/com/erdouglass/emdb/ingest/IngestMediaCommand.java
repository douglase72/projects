package com.erdouglass.emdb.ingest;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record IngestMediaCommand(Integer tmdbId, IngestType ingestType) {

  public IngestMediaCommand {
    Objects.requireNonNull(tmdbId, "TMDB id is required");
    Objects.requireNonNull(ingestType, "ingest type is required");
  }
  
  public static IngestMediaCommand of(Integer tmdbId, IngestType ingestType) {
    return new IngestMediaCommand(tmdbId, ingestType);
  }
  
  public enum IngestType {
    MOVIE("movie"), 
    PERSON("person"),
    SERIES("series");
    
    private static final Map<String, IngestType> LOOKUP = Stream.of(values())
        .collect(Collectors.toMap(Object::toString, Function.identity()));
    
    private final String type;
    
    IngestType(String type) {
      this.type = type;
    }
    
    public static IngestType from(String type) {
      Objects.requireNonNull(type, "type is required");
      var result = LOOKUP.get(type.toLowerCase().trim());
      if (result == null) {
        throw new IllegalArgumentException("invalid ingest type: " + type);
      }
      return result;
    }
    
    @Override
    public String toString() {
      return type;
    }
  }
}
