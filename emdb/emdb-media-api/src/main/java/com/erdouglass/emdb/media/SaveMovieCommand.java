package com.erdouglass.emdb.media;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.erdouglass.common.validation.DateRange;

public record SaveMovieCommand(
    @NotNull @Valid SourceId sourceId,
    @NotBlank @Size(max = MediaConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = MediaConstants.MOVIE_MIN_DATE, max = MediaConstants.MAX_DATE) LocalDate releaseDate) {

  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private SourceId sourceId;
    private LocalDate releaseDate;
    private String title;
    
    private Builder() {}

    public SaveMovieCommand build() {
      return new SaveMovieCommand(sourceId, title, releaseDate);
    }
    
    public Builder sourceId(SourceId sourceId) {
      this.sourceId = sourceId;
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