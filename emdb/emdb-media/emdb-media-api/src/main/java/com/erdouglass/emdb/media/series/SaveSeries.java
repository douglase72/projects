package com.erdouglass.emdb.media.series;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.media.Image;
import com.erdouglass.emdb.media.person.Gender;
import com.erdouglass.emdb.media.person.PersonConstants;
import com.erdouglass.emdb.media.person.PersonCredit;
import com.erdouglass.emdb.media.show.SaveShow;
import com.erdouglass.emdb.media.show.ShowConstants;
import com.erdouglass.emdb.media.show.ShowStatus;

public record SaveSeries(
    @NotNull @Positive Integer tmdbId,
    @NotBlank @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @NotNull @Min(0) @Max(10) Float score,
    @NotNull ShowStatus status,
    @NotNull SeriesType type,
    @Valid Image backdrop,
    @Valid Image poster,
    @Size(min = 1, max = Configuration.URL_MAX_LENGTH) String homepage,
    @NotBlank @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) String originalLanguage,
    @Size(max = ShowConstants.TAGLINE_MAX_LENGTH) String tagline,
    @Size(min = 1, max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @NotNull @Valid Credits credits) implements SaveShow {

  public static Builder builder() {
    return new Builder();
  }
  
  public static Builder builder(final SaveSeries command) {
    return builder()
        .tmdbId(command.tmdbId)
        .title(command.title)
        .score(command.score)
        .status(command.status)
        .type(command.type)
        .backdrop(command.backdrop)
        .poster(command.poster)
        .homepage(command.homepage)
        .originalLanguage(command.originalLanguage)
        .tagline(command.tagline)
        .overview(command.overview)
        .credits(command.credits);
  }
  
  @Override
  public String toString() {
    return "SaveSeries[tmdbId=" + tmdbId
        + ", title=" + title
        + "]";
  }
  
  public record Credits(List<CastCredit> cast, List<CrewCredit> crew) {}
  
  public record CastCredit(
      @NotNull @Positive Integer tmdbId,
      @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
      @NotNull Gender gender,
      @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile,      
      @NotEmpty List<@Valid Role> roles,
      @NotNull @PositiveOrZero Integer order) implements com.erdouglass.emdb.media.credit.CastCredit {
    
    public record Role(
        @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character,
        @NotNull @PositiveOrZero Integer episodeCount) {}
  }
  
  public record CrewCredit(
      @NotNull @Positive Integer tmdbId,
      @NotBlank @Size(max = PersonConstants.NAME_MAX_LENGTH) String name,
      @NotNull Gender gender,
      @Size(min = PersonConstants.PROFILE_MIN_LENGTH, max = PersonConstants.PROFILE_MAX_LENGTH) String profile,     
      @NotEmpty List<@Valid Job> jobs) implements PersonCredit {
    
    public record Job(
        @Size(max = ShowConstants.ROLE_MAX_LENGTH) String title,
        @NotNull @PositiveOrZero Integer episodeCount) {}    
  }  
  
  public static final class Builder extends SeriesBuilder<Builder> {
    private Credits credits = new Credits(List.of(), List.of());
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
            credits);
    }
    
    public Builder credits(final Credits credits) {
      this.credits = credits;
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
