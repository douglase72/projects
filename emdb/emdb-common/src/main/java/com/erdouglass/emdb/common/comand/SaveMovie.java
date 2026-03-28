package com.erdouglass.emdb.common.comand;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
import com.erdouglass.validation.DateRange;

public record SaveMovie(
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
    @Min(0) @Max(10) Float score,
    ShowStatus status,
    @PositiveOrZero Integer runtime,
    @PositiveOrZero Integer budget,
    @PositiveOrZero Integer revenue,
    Image backdrop,
    Image poster,
    @Size(min = 1, max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @NotNull @Valid Credits credits,
    @NotNull List<@Valid SavePerson> people) {
  
  public record Credits(List<@Valid CastCredit> cast, List<@Valid CrewCredit> crew) { }  
  
  public record CastCredit(
      @NotNull @Positive Integer tmdbId,
      @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character,
      @NotNull @PositiveOrZero Integer order) {
    
  }
  
  public record CrewCredit(
      @NotNull @Positive Integer tmdbId,
      @Size(max = ShowConstants.ROLE_MAX_LENGTH) String job) {
    
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder extends AbstractMovieBuilder<Builder> {
    private Image backdrop;
    private Credits credits = new Credits(List.of(), List.of());
    private List<SavePerson> people = new ArrayList<>();
    private Image poster;
    private Integer tmdbId;
    
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
            backdrop,
            poster,
            homepage,
            originalLanguage,
            tagline,
            overview,
            credits,
            people);
    }
    
    public Builder backdrop(Image backdrop) {
      this.backdrop = backdrop;
      return this;
    }
    
    public Builder credits(Credits credits) {
      this.credits = credits;
      return this;
    }    
    
    public Builder people(List<SavePerson> people) {
      this.people = new ArrayList<>(people);
      return this;
    }
    
    public Builder poster(Image poster) {
      this.poster = poster;
      return this;
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
