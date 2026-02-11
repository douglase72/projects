package com.erdouglass.emdb.common.query;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import com.erdouglass.emdb.common.EmdbImage;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.validation.DateRange;

public record MovieDto(
    @NotNull @Positive Long id,
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
    @Min(0) @Max(10) Float score,
    ShowStatus status,
    @PositiveOrZero Integer runtime,
    @PositiveOrZero Integer budget,
    @PositiveOrZero Integer revenue,
    @Size(max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @EmdbImage String backdrop,
    @EmdbImage String poster,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline, 
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @Valid Credits credits) {
  
  public record Credits(List<@Valid MovieCreditDto> cast, List<@Valid MovieCreditDto> crew) {
    
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public List<MovieCreditDto> cast() {
    return Optional.ofNullable(credits).map(c -> c.cast().stream().toList()).orElse(List.of());
  }
  
  public List<MovieCreditDto> crew() {
    return Optional.ofNullable(credits).map(c -> c.crew().stream().toList()).orElse(List.of());
  }  
  
  @Override
  public String toString() {
    return "MovieDto[id=" + id
            + ", tmdbId=" + tmdbId
            + ", title=" + title
            + ", releaseDate=" + releaseDate
            + "]";
  }
  
  public static final class Builder extends AbstractMovieBuilder<Builder> {
    private List<MovieCreditDto> cast = new ArrayList<>();
    private List<MovieCreditDto> crew = new ArrayList<>();    
    private Integer tmdbId;
    private String backdrop;
    private Long id;
    private String poster;

    private Builder() { }

    public MovieDto build() {
      Credits credits = null;
      if (!cast.isEmpty() || !crew.isEmpty()) {
        credits = new Credits(cast.stream().toList(), crew.stream().toList());
      }      
      return new MovieDto(
            id,
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
            overview,
            credits);
    }
    
    public Builder backdrop(String backdrop) {
      this.backdrop = backdrop;
      return this;
    }
    
    public Builder cast(List<MovieCreditDto> cast) {
      this.cast = new ArrayList<>(cast);
      return this;
    }
    
    public Builder crew(List<MovieCreditDto> crew) {
      this.crew = new ArrayList<>(crew);
      return this;
    }     

    public Builder id(Long id) {
      this.id = id;
      return this;
    }
    
    public Builder poster(String poster) {
      this.poster = poster;
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
