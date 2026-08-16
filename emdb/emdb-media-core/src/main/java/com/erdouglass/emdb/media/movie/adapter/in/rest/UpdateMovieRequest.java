package com.erdouglass.emdb.media.movie.adapter.in.rest;

import java.math.BigDecimal;
import java.util.Optional;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.kernel.Title;

/// Request body for editing a title through the catalogue id.
///
/// Identical to [SaveMovieRequest] apart from the required version, and the
/// version is the whole difference in behaviour: this endpoint refuses the write
/// if the title has moved on since the client read it, where ingestion does not.
///
/// Replacement semantics apply here too — an omitted optional field clears the
/// stored value rather than preserving it.
///
/// @param version the version the client last read
/// @param title the display title, required
/// @param releaseDate the release date in ISO-8601 form, empty to clear
/// @param score the rating from 0 to 10, empty to clear
/// @param originalLanguage the ISO 639-1 code, empty to clear
/// @param overview the synopsis, empty to clear
public record UpdateMovieRequest(
    @NotNull @PositiveOrZero Long version, 
    @NotBlank @Size(max = Title.MAX_LENGTH) String title,
    Optional<String> releaseDate,
    Optional<@Min(0) @Max(10) BigDecimal> score,
    Optional<@Pattern(regexp = "[a-z]{2}") String> originalLanguage,
    Optional<String> overview) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private Long version;
    private String title;
    private String releaseDate;
    private BigDecimal score;
    private String originalLanguage;
    private String overview;
    
    private Builder() {}

    public UpdateMovieRequest build() {
      return new UpdateMovieRequest(
          version,
          title, 
          Optional.ofNullable(releaseDate), 
          Optional.ofNullable(score),
          Optional.ofNullable(originalLanguage),
          Optional.ofNullable(overview));
    }
    
    public Builder originalLanguage(String originalLanguage) {
      this.originalLanguage =originalLanguage;
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
    
    public Builder version(Long version) {
      this.version = version;
      return this;
    }
  }  
}
