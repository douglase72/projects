package com.erdouglass.emdb.common.command;

import java.util.Optional;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;

public record SeriesUpdateCommand(
    Optional<@Size(max = ShowConstants.NAME_MAX_LENGTH) String> name,
    Optional<@Min(0) @Max(10) Float> score,
    Optional<ShowStatus> status,
    Optional<@Size(max = ShowConstants.SERIES_TYPE_MAX_LENGTH) String> type,
    Optional<@Size(max = Configuration.URL_MAX_LENGTH) String> homepage,
    Optional<@Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String> originalLanguage,
    Optional<@Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String> backdrop,
    Optional<@Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String> poster,
    Optional<@Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String> tagline,
    Optional<@Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String> overview) {

  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private Optional<String> name = Optional.empty();
    private Optional<Float> score = Optional.empty();
    private Optional<ShowStatus> status = Optional.empty();
    private Optional<String> type = Optional.empty();
    private Optional<String> homepage = Optional.empty();
    private Optional<String> originalLanguage = Optional.empty();
    private Optional<String> backdrop = Optional.empty();
    private Optional<String> poster = Optional.empty();
    private Optional<String> tagline = Optional.empty();
    private Optional<String> overview = Optional.empty();   
    
    private Builder() { }
    
    public SeriesUpdateCommand build() {
      return new SeriesUpdateCommand(
          name,
          score,
          status,
          type,
          homepage,
          originalLanguage,
          backdrop,
          poster,
          tagline,
          overview);
    }
    
    public Builder name(String name) {
      this.name = Optional.ofNullable(name);
      return this;
    }
    
    public Builder score(Float score) {
      this.score = Optional.ofNullable(score);
      return this;
    }
    
    public Builder status(ShowStatus status) {
      this.status = Optional.ofNullable(status);
      return this;
    }
    
    public Builder type(String type) {
      this.type = Optional.ofNullable(type);
      return this;
    }
    
    public Builder homepage(String homepage) {
      this.homepage = Optional.ofNullable(homepage);
      return this;
    }
    
    public Builder originalLanguage(String originalLanguage) {
      this.originalLanguage = Optional.ofNullable(originalLanguage);
      return this;
    }
    
    public Builder backdrop(String backdrop) {
      this.backdrop = Optional.ofNullable(backdrop);
      return this;
    }
    
    public Builder poster(String poster) {
      this.poster = Optional.ofNullable(poster);
      return this;
    }
    
    public Builder tagline(String tagline) {
      this.tagline = Optional.ofNullable(tagline);
      return this;
    }
    
    public Builder overview(String overview) {
      this.overview = Optional.ofNullable(overview);
      return this;
    }
  }
  
}
