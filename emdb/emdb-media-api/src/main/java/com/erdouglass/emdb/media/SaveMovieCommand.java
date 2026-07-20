package com.erdouglass.emdb.media;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.erdouglass.common.validation.DateRange;

/// Inbound message of [SaveMovieUseCase]: the raw upsert payload as an
/// external source supplies it. Bean Validation here is transport hygiene —
/// constraints fire only where a validator runs (the REST adapter's
/// `@Valid`); driving adapters calling the port directly bypass them, and
/// the authoritative rules run regardless in the value objects. Strings and
/// dates only: value objects are minted inside the hexagon, not before it.
public record SaveMovieCommand(
    @NotBlank String source,
    @NotBlank String sourceId,
    @NotBlank @Size(max = MediaConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = MediaConstants.MOVIE_MIN_DATE, max = MediaConstants.MAX_DATE) LocalDate releaseDate,
    @NotBlank @Size(min = MediaConstants.ISO_639_1_LENGTH, max = MediaConstants.ISO_639_1_LENGTH) String originalLanguage) {

  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private String source;
    private String sourceId;
    private LocalDate releaseDate;
    private String title;
    private String originalLanguage;
    
    private Builder() {}

    public SaveMovieCommand build() {
      return new SaveMovieCommand(
          source,
          sourceId, 
          title, 
          releaseDate, 
          originalLanguage);
    }
    
    public Builder originalLanguage(final String originalLanguage) {
      this.originalLanguage =originalLanguage;
      return this;
    }
    
    /// Sets the provenance pair in one call — provider token and provider
    /// id — since neither means anything alone.
    public Builder sourceId(String source, String id) {
      this.source = source;
      this.sourceId = id;
      return this;
    }
    
    public Builder releaseDate(LocalDate releaseDate) {
      this.releaseDate = releaseDate;
      return this;
    }
    
    public Builder title(String title) {
      this.title = title;
      return this;
    }
  }
}