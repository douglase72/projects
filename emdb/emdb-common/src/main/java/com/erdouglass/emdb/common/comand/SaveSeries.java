package com.erdouglass.emdb.common.comand;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.AbstractSeriesBuilder;
import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.SeriesType;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;

public record SaveSeries(
    @NotNull @Positive Integer tmdbId,
    @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @Min(0) @Max(10) Float score,
    ShowStatus status,
    SeriesType type,
    UUID backdrop,
    UUID poster,
    @Size(min = 1, max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline, 
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @NotNull @Valid Credits credits,
    @NotNull List<@Valid SavePerson> people) {

  public record Credits(List<@Valid CastCredit> cast, List<@Valid CrewCredit> crew) { }  
  
  public record CastCredit(
      @NotNull @Positive Integer tmdbId,
      @NotEmpty List<@Valid Role> roles,
      @NotNull @PositiveOrZero Integer order) {
    
    public record Role(
        @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character,
        @NotNull @PositiveOrZero Integer episodeCount) {
    }
  }
  
  public record CrewCredit(
      @NotNull @Positive Integer tmdbId,
      @NotEmpty List<@Valid Job> jobs) {
    
    public record Job(
        @Size(max = ShowConstants.ROLE_MAX_LENGTH) String title,
        @NotNull @PositiveOrZero Integer episodeCount) {
    }    
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder extends AbstractSeriesBuilder<Builder> {
    private UUID backdrop;
    private Credits credits;
    private List<SavePerson> people = new ArrayList<>();
    private UUID poster;
    private Integer tmdbId;
        
    private Builder() { }

    public SaveSeries build() {
      return new SaveSeries(
            tmdbId,
            title, 
            score,
            status,
            type,
            backdrop,
            poster,            
            homepage,
            originalLanguage,
            tagline,
            overview,
            credits,
            people);
    }
    
    public Builder backdrop(UUID backdrop) {
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
    
    public Builder poster(UUID poster) {
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
