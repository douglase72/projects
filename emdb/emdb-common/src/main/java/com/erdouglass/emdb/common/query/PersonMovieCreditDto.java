package com.erdouglass.emdb.common.query;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.EmdbImage;
import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.validation.DateRange;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public record PersonMovieCreditDto(    
    @NotNull UUID creditId, 
    @NotNull @Positive Long id,
    @Size(max = ShowConstants.TITLE_MAX_LENGTH) String title,
    @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character,
    @Size(max = ShowConstants.ROLE_MAX_LENGTH) String job,
    @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE) LocalDate releaseDate,
    @Min(0) @Max(10) Float score,
    @EmdbImage String backdrop,
    @EmdbImage String poster,
    @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH) String overview,
    @NotNull MediaType type) implements PersonCreditDto {

  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private String backdrop;
    private String character;
    private UUID creditId;
    private Long id;
    private String job;
    private String overview;
    private String poster;
    private LocalDate releaseDate;
    private Float score;
    private String title;
    private MediaType type;
    
    private Builder() { }
    
    public PersonMovieCreditDto build() {
      return new PersonMovieCreditDto(
          creditId,
          id,
          title,
          character,
          job,
          releaseDate,
          score,
          backdrop,
          poster,
          overview,
          type);
    }
    
    public Builder backdrop(String backdrop) {
      this.backdrop = backdrop;
      return this;
    }
    
    public Builder character(String character) {
      this.character = character;
      return this;
    }
    
    public Builder creditId(UUID creditId) {
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
    
    public Builder type(MediaType type) {
      this.type = type;
      return this;
    }   
  }
  
}
