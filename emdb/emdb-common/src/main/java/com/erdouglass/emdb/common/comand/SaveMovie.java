package com.erdouglass.emdb.common.comand;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.AbstractMovieBuilder;
import com.erdouglass.emdb.common.ShowConstants;

/// Command payload containing the necessary data to save a new movie.
///
/// Includes validation constraints to ensure data integrity before persistence.
///
/// @param tmdbId           the strictly positive external TMDB identifier
/// @param title            the title of the movie
/// @param releaseDate      the official release date, bounded by [ShowConstants]
/// @param score            the user or critic score, ranging from 0 to 10
/// @param status           the current production [ShowStatus]
/// @param runtime          the length of the movie in minutes
/// @param budget           the production budget
/// @param revenue          the box office revenue
/// @param homepage         the official website URL
/// @param originalLanguage the ISO 639-1 language code
/// @param backdrop         the internal UUID of the backdrop image
/// @param tmdbBackdrop     the TMDB path for the backdrop image
/// @param poster           the internal UUID of the poster image
/// @param tmdbPoster       the TMDB path for the poster image
/// @param tagline          a short promotional tagline
/// @param overview         a detailed plot summary
public record SaveMovie(
    @NotNull @Positive Integer tmdbId,
    @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    LocalDate releaseDate) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  @Override
  public String toString() {
    return "SaveMovie[tmdbId=" + tmdbId
            + ", title=" + title
            + "]";
  }
  
  public static final class Builder extends AbstractMovieBuilder<Builder> {
    private Integer tmdbId;
    
    private Builder() { }

    public SaveMovie build() {
      return new SaveMovie(
            tmdbId,
            title, 
            releaseDate);
    }
    
    public Builder tmdbId(Integer tmdbId) {
      this.tmdbId = tmdbId;
      return this;
    }

    @Override
    protected Builder self() {
      return this;
    }
  }  

}
