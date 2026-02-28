package com.erdouglass.emdb.common.comand;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @Size(max = Configuration.URL_MAX_LENGTH) String homepage,
    @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    UUID backdrop,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String tmdbBackdrop,
    UUID poster,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String tmdbPoster,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline, 
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @Valid Credits credits) {
  
  public record Credits(List<@Valid SaveSeriesCastCredit> cast, List<@Valid SaveSeriesCrewCredit> crew) {
    
  }  
  
  public static Builder builder() {
    return new Builder();
  }
  
  public List<SavePerson> people() {
    if (credits() == null) {
      return List.of();
    }    
    return Stream.concat(
        credits().cast().stream().map(SaveSeriesCastCredit::person),
        credits().crew().stream().map(SaveSeriesCrewCredit::person)
    ).collect(Collectors.toMap(SavePerson::tmdbId, p -> p, (existing, _) -> existing)).values().stream().toList();
  }
  
  @Override
  public String toString() {
    return "SaveSeries[tmdbId=" + tmdbId
            + ", title=" + title
            + "]";
  }
  
  public static final class Builder extends AbstractSeriesBuilder<Builder> {
    private Credits credits;
    private Integer tmdbId;
    private UUID backdrop;
    private UUID poster;
    private String tmdbBackdrop;
    private String tmdbPoster;
        
    private Builder() { }

    public SaveSeries build() {
      return new SaveSeries(
            tmdbId,
            title, 
            score,
            status,
            type,
            homepage,
            originalLanguage,
            backdrop,
            tmdbBackdrop,
            poster,
            tmdbPoster,
            tagline,
            overview,
            credits);
    }
    
    public Builder backdrop(UUID backdrop) {
      this.backdrop = backdrop;
      return this;
    }
    
    public Builder tmdbBackdrop(String tmdbBackdrop) {
      this.tmdbBackdrop = tmdbBackdrop;
      return this;
    }
    
    public Builder credits(Credits credits) {
      this.credits = credits;
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
  