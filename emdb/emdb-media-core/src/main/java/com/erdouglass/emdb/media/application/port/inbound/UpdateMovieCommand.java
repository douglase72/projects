package com.erdouglass.emdb.media.application.port.inbound;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.MediaConstants;

/// The REST dialect's edit payload for `PUT /movies/{id}`: JSON shape and
/// Bean Validation, implementing the domain's [UpdateMovie] contract so the
/// record reaches the aggregate without the domain naming a transport type.
/// Each future driving adapter supplies its own implementation — the
/// interface is the shared contract, the shape is per-dialect.
///
/// Carries no id on purpose: a PUT's URI *is* the address, and a body copy
/// would be a second source of truth to police.
public record UpdateMovieCommand(
    @NotNull @PositiveOrZero Long version, 
    @NotBlank @Size(max = MediaConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = MediaConstants.MOVIE_MIN_DATE, max = MediaConstants.MAX_DATE) LocalDate releaseDate,
    @NotBlank 
    @Size(min = MediaConstants.ISO_639_1_LENGTH, max = MediaConstants.ISO_639_1_LENGTH) 
    String originalLanguage) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private Long version;
    private String title;
    private LocalDate releaseDate;
    private String originalLanguage;
    
    private Builder() {}

    public UpdateMovieCommand build() {
      return new UpdateMovieCommand(
          version,
          title, 
          releaseDate, 
          originalLanguage);
    }
    
    public Builder originalLanguage(final String originalLanguage) {
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
