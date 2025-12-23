package com.erdouglass.emdb.common.query;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.validation.DateRange;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public record PersonCreditDto(
    @NotNull @Positive Long creditId, 
    @NotNull @Positive Long id,
    @Size(max = ShowConstants.NAME_MAX_LENGTH) String title,
    @Size(max = ShowConstants.NAME_MAX_LENGTH) String name,
    @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character,
    @Size(max = ShowConstants.ROLE_MAX_LENGTH) String job,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
    @DateRange(min = ShowConstants.SERIES_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate firstAirDate,
    @Min(0) @Max(10) Float score,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String backdrop,
    @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH) String poster,
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private String backdrop;
    private String character;
    private Long creditId;
    private LocalDate firstAirDate;
    private Long id;
    private String job;
    private String name;
    private String overview;
    private String poster;
    private LocalDate releaseDate;
    private Float score;
    private String title;
    
    private Builder() { }
    
    public PersonCreditDto build() {
      return new PersonCreditDto(
          creditId,
          id,
          title,
          name,
          character,
          job,
          releaseDate,
          firstAirDate,
          score,
          backdrop,
          poster,
          overview);
    }
    
    public Builder backdrop(String backdrop) {
      this.backdrop = backdrop;
      return this;
    }
    
    public Builder character(String character) {
      this.character = character;
      return this;
    }
    
    public Builder creditId(Long creditId) {
      this.creditId = creditId;
      return this;
    }
    
    public Builder id(Long id) {
      this.id = id;
      return this;
    }
    
    public Builder job(String job) {
      this.job = job;
      return this;
    }
    
    public Builder name(String name) {
      this.name = name;
      return this;
    }
    
    public Builder overview(String overview) {
      this.overview = overview;
      return this;
    }
    
    public Builder poster(String poster) {
      this.poster = poster;
      return this;
    }
    
    public Builder releaseDate(LocalDate releaseDate) {
      this.releaseDate = releaseDate;
      return this;
    }
    
    public Builder score(Float score) {
      this.score = score;
      return this;
    }
    
    public Builder title(String title) {
      this.title = title;
      return this;
    }
  }

}
