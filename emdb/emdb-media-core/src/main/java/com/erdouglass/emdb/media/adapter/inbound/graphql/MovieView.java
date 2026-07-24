package com.erdouglass.emdb.media.adapter.inbound.graphql;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;

@Name("Movie")
public record MovieView(
    @NonNull String id,
    @NonNull Long version,
    @NonNull String title,
    String releaseDate,
    @NonNull String originalLanguage) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private String id;
    private Long version;
    private String title;
    private String releaseDate;
    private String originalLanguage;
    
    private Builder() {}

    public MovieView build() {
      Objects.requireNonNull(id, "id must not be null");
      Objects.requireNonNull(version, "version must not be null");
      Objects.requireNonNull(title, "title must not be null");
      Objects.requireNonNull(originalLanguage, "original language must not be null");
      return new MovieView(
          id,
          version,
          title, 
          releaseDate, 
          originalLanguage);
    }
    
    public Builder id(final String id) {
      this.id = id;
      return this;
    }    
    
    public Builder originalLanguage(final String originalLanguage) {
      this.originalLanguage = originalLanguage;
      return this;
    }
    
    public Builder releaseDate(String releaseDate) {
      this.releaseDate = releaseDate;
      return this;
    }
    
    public Builder title(String title) {
      this.title = title;
      return this;
    }
    
    public Builder version(Long version) {
      this.version = version;
      return this;
    }
  }
}
