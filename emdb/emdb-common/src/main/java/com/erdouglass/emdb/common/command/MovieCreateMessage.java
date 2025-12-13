package com.erdouglass.emdb.common.command;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.AbstractMovieBuilder;
import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.emdb.common.command.AuditMetadata.EventSource;
import com.erdouglass.emdb.common.command.AuditMetadata.EventType;
import com.erdouglass.validation.DateRange;

public record MovieCreateMessage(
    @NotNull @Valid AuditMetadata meta,
	@NotNull @Positive Integer tmdbId,
	@NotBlank @Size(max = ShowConstants.NAME_MAX_LENGTH) String title,
	@DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
	@Min(0) @Max(10) Float score,
	@NotNull ShowStatus status,
	@PositiveOrZero Integer runtime,
	@PositiveOrZero Integer budget,
	@PositiveOrZero Integer revenue,
	@Size(max = Configuration.URL_MAX_LENGTH) String homepage,
	@Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String backdrop,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String poster,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline, 
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  @Override
  public String toString() {
    return "MovieCreateMessage[meta=" + meta
            + ", tmdbId=" + tmdbId
            + ", title=" + title
            + ", releaseDate=" + releaseDate
            + "]";
  } 
  
  public static final class Builder extends AbstractMovieBuilder<Builder> {
    private String traceId;
    private EventSource source;
    private EventType type;
    private String message;
    private Integer percentComplete = 0;
    
    private Builder() { }

    public MovieCreateMessage build() {
      return new MovieCreateMessage(
          new AuditMetadata(traceId, Instant.now(), source, type, message, percentComplete, null),
          tmdbId,
          title, 
          releaseDate,
          score,
          status,
          runtime,
          budget,
          revenue,
          homepage,
          originalLanguage,
          backdrop,
          poster,
          tagline,
          overview);
    }
    
    public Builder message(String message) {
      this.message = message;
      return this;
    }
    
    public Builder percentComplete(Integer percentComplete) {
      this.percentComplete = percentComplete;
      return this;
    }
    
    public Builder source(EventSource source) {
      this.source = source;
      return this;
    }
    
    public Builder traceId(String traceId) {
      this.traceId = traceId;
      return this;
    }
    
    public Builder type(EventType type) {
      this.type = type;
      return this;
    }

    @Override
    protected Builder self() {
      return this;
    }
  }

}
