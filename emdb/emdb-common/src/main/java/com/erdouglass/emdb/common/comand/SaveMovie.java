package com.erdouglass.emdb.common.comand;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.AbstractMovieBuilder;
import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.validation.DateRange;

public record SaveMovie(
    @NotNull @Positive Integer tmdbId,
    @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
    @Min(0) @Max(10) Float score,
    ShowStatus status,
    @PositiveOrZero Integer runtime,
    @PositiveOrZero Integer budget,
    @PositiveOrZero Integer revenue,
    @Size(min = 1, max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    UUID backdrop,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String tmdbBackdrop,
    UUID poster,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String tmdbPoster,
    @Size(min = 1, max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @NotNull List<@Valid SavePerson> people) {
  
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
    private UUID backdrop;
    private UUID poster;
    private String tmdbBackdrop;
    private String tmdbPoster;
    private List<SavePerson> people = new ArrayList<>();
    
    private Builder() { }

    public SaveMovie build() {
      return new SaveMovie(
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
            tmdbBackdrop,
            poster,
            tmdbPoster,
            tagline,
            overview,
            people);
    }
    
    public Builder backdrop(UUID backdrop) {
      this.backdrop = backdrop;
      return this;
    }
    
    public Builder tmdbBackdrop(String tmdbBackdrop) {
      this.tmdbBackdrop = tmdbBackdrop;
      return this;
    }
    
    public Builder people(Collection<SavePerson> people) {
      this.people = List.copyOf(people);
      return this;
    }
    
    public Builder poster(UUID poster) {
      this.poster = poster;
      return this;
    }
    
    public Builder tmdbPoster(String tmdbPoster) {
      this.tmdbPoster = tmdbPoster;
      return this;
    }  

    @Override
    protected Builder self() {
      return this;
    }
  }  

}
