package com.erdouglass.emdb.media.core.movie;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.PositiveOrZero;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.core.Media;
import com.erdouglass.emdb.media.core.Show;
import com.erdouglass.emdb.media.show.ShowConstants;

@Entity
@Table(
  name = "Movies",
  uniqueConstraints = @UniqueConstraint(
    name = "uk_movies_title_release_date",
    columnNames = { "title", "release_date" }
  )
)
@SequenceGenerator(
  name = Media.SEQUENCE_GENERATOR, 
  sequenceName = "movie_sequence", 
  initialValue = 1, 
  allocationSize = 1)
class Movie extends Show {
  @PositiveOrZero
  private Long budget;
  
  @Column(name = "release_date")
  @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE)
  private LocalDate releaseDate;
  
  @PositiveOrZero
  private Long revenue;
  
  @PositiveOrZero
  private Integer runtime;
  
  /// Default constructor required by JPA.
  protected Movie() {}
  
  protected Movie(int tmdbId) {
    super(tmdbId);
  }
  
  public void setBudget(Long budget) {
    this.budget = budget;
  }

  public Long getBudget() {
    return budget;
  }
  
  public void setReleaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }
  
  public LocalDate getReleaseDate() {
    return releaseDate;
  }
  
  public void setRevenue(Long revenue) {
    this.revenue = revenue;
  }

  public Long getRevenue() {
    return revenue;
  }

  public void setRuntime(Integer runtime) {
    this.runtime = runtime;
  }

  public Integer getRuntime() {
    return runtime;
  }  
  
  @Override
  public String toString() {
    return "Movie[id=" + getId()
        + ", tmdbId=" + getTmdbId()
        + ", title=" + getTitle() 
        + ", releaseDate=" + getReleaseDate()
        + ", poster=" + getPoster()
        + "]";
  }
}
