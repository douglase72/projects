package com.erdouglass.emdb.common.command;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.erdouglass.emdb.common.AbstractMovieBuilder;
import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.validation.DateRange;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record MovieCreateCommand(
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
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @NotNull @Valid List<MovieCreditCreateCommand> credits,
    @NotNull @Valid List<PersonCreateCommand> people) {
	
  public static Builder builder() {
    return new Builder();
  }
  
  @Override
  public String toString() {
  	return "MovieCreateCommand[tmdbId=" + tmdbId
  			+ ", title=" + title
  			+ ", releaseDate=" + releaseDate
  	        + ", credits=" + credits.size() 
  	        + ", people=" + people.size() 
  			+ "]";
  }
  
  public static final class Builder extends AbstractMovieBuilder<Builder> {
    private List<MovieCreditCreateCommand> credits = new ArrayList<>();
    private List<PersonCreateCommand> people = new ArrayList<>();

    private Builder() { }

    public MovieCreateCommand build() {
      return new MovieCreateCommand(
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
      		credits,
      		people);
    }
    
    public Builder credits(List<MovieCreditCreateCommand> credits) {
      this.credits = new ArrayList<>(credits);
      return this;
    }
    
    public Builder people(List<PersonCreateCommand> people) {
      this.people = new ArrayList<>(people);
      return this;
    }

    @Override
    protected Builder self() {
      return this;
    }
  }

}
