package com.erdouglass.emdb.media.movie.adapter.in.rest;

import java.math.BigDecimal;
import java.util.Optional;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.kernel.Title;

/// Request body for the ingestion endpoint.
///
/// Carries no version and no id: the TMDB id comes from the path, and ingestion
/// deliberately skips the staleness check so that a feed can replay without
/// coordinating with concurrent editors.
///
/// `Optional` components are used so that the endpoint can express clearing a
/// field. An omitted field is not preserved — the request describes the whole
/// title, and anything left out is removed.
///
/// Constraints here duplicate limits enforced by the domain value objects on
/// purpose: catching a malformed body at the boundary yields a `400` with field
/// paths, rather than an exception mapped after the request has already reached
/// the service.
///
/// @param title the display title, required
/// @param releaseDate the release date in ISO-8601 form, empty to clear
/// @param score the rating from 0 to 10, empty to clear
/// @param originalLanguage the ISO 639-1 code, empty to clear
/// @param overview the synopsis, empty to clear
public record SaveMovieRequest(
    @NotBlank @Size(max = Title.MAX_LENGTH) String title,
    Optional<String> releaseDate,
    Optional<@Min(0) @Max(10) BigDecimal> score,
    Optional<@Pattern(regexp = "[a-z]{2}") String> originalLanguage,
    Optional<String> overview) {

  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private String title;
    private String releaseDate;
    private BigDecimal score;
    private String originalLanguage;
    private String overview;
    
    private Builder() {}

    public SaveMovieRequest build() {
      return new SaveMovieRequest(
          title, 
          Optional.ofNullable(releaseDate), 
          Optional.ofNullable(score),
          Optional.ofNullable(originalLanguage),
          Optional.ofNullable(overview));
    }
    
    public Builder originalLanguage(String originalLanguage) {
      this.originalLanguage = originalLanguage;
      return this;
    }
    
    public Builder overview(String overview) {
      this.overview = overview;
      return this;
    }    
    
    public Builder releaseDate(String releaseDate) {
      this.releaseDate = releaseDate;
      return this;
    }
    
    public Builder score(BigDecimal score) {
      this.score = score;
      return this;
    }    
    
    public Builder title(String title) {
      this.title = title;
      return this;
    }
  }  
}
