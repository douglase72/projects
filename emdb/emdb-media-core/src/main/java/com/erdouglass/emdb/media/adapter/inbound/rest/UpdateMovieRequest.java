package com.erdouglass.emdb.media.adapter.inbound.rest;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.MediaConstants;

public record UpdateMovieRequest(
    @NotNull @PositiveOrZero Long version, 
    @NotBlank @Size(max = MediaConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = MediaConstants.MOVIE_MIN_DATE, max = MediaConstants.MAX_DATE) LocalDate releaseDate,
    @NotBlank @Size(min = MediaConstants.ISO_639_1_LENGTH, max = MediaConstants.ISO_639_1_LENGTH) String originalLanguage) {

  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private Long version;
    private String title;
    private LocalDate releaseDate;
    private String originalLanguage;
    
    private Builder() {}

    public UpdateMovieRequest build() {
      return new UpdateMovieRequest(
          version,
          title, 
          releaseDate, 
          originalLanguage);
    }
    
    public Builder originalLanguage(String originalLanguage) {
      this.originalLanguage =originalLanguage;
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
    
    public Builder version(Long version) {
      this.version = version;
      return this;
    }
  }  
}
