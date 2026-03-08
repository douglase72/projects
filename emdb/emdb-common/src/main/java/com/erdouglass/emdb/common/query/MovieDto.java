package com.erdouglass.emdb.common.query;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.AbstractMovieBuilder;
import com.erdouglass.emdb.common.ShowConstants;

/// Data Transfer Object (DTO) representing a movie entity.
///
/// Used for querying and returning movie data to the client. Images are represented 
/// as standard string paths/filenames rather than raw UUIDs.
///
/// @param id               the internal database identifier
/// @param tmdbId           the external TMDB identifier
/// @param title            the title of the movie
/// @param releaseDate      the official release date
/// @param score            the user or critic score, ranging from 0 to 10
/// @param status           the current production [ShowStatus]
/// @param runtime          the length of the movie in minutes
/// @param budget           the production budget
/// @param revenue          the box office revenue
/// @param homepage         the official website URL
/// @param originalLanguage the ISO 639-1 language code
/// @param backdrop         the validated filename of the backdrop image
/// @param poster           the validated filename of the poster image
/// @param tagline          a short promotional tagline
/// @param overview         a detailed plot summary
public record MovieDto(
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    LocalDate releaseDate) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder extends AbstractMovieBuilder<Builder> {
    private Integer tmdbId;
    private Long id;

    private Builder() { }

    public MovieDto build() { 
      return new MovieDto(
            id,
            tmdbId,
            title, 
            releaseDate);
    }

    public Builder id(Long id) {
      this.id = id;
      return this;
    }
    
    public Builder tmdbId(Integer tmdbId) {
      this.tmdbId = tmdbId;
      return self();
    }

    @Override
    protected Builder self() {
      return this;
    }
  } 

}
