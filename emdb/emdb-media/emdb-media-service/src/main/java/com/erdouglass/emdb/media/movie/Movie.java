package com.erdouglass.emdb.media.movie;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.PositiveOrZero;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.internal.Media;
import com.erdouglass.emdb.media.internal.Show;
import com.erdouglass.emdb.media.show.ShowConstants;

/// A theatrical film entity. Extends [Show] with movie-specific fields
/// (budget, revenue, runtime, release date) and binds the inherited sequence
/// generator to the `movie_sequence` database sequence.
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
  
  /// The credits collection in a movie is a bidirectional association 
  /// specified by the mappedBy field which maps the {@link Movie#id} 
  /// primary key to the foreign key in the Credits table.
  @OneToMany(mappedBy = _MovieCredit.MOVIE)
  private List<MovieCredit> credits = new ArrayList<>(); 
  
  @Column(name = "release_date")
  @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE)
  private LocalDate releaseDate;
  
  @PositiveOrZero
  private Long revenue;
  
  @PositiveOrZero
  private Integer runtime;
  
  /// Default constructor required by JPA.
  protected Movie() {}
  
  protected Movie(final int tmdbId) {
    super(tmdbId);
  }
  
  public void setBudget(final Long budget) {
    this.budget = budget;
  }

  public Long getBudget() {
    return budget;
  }  
  
  public void setCredits(final List<MovieCredit> credits) {
    this.credits = new ArrayList<>(credits);
  }
  
  public List<MovieCredit> getCredits() {
    return List.copyOf(credits);
  }  
  
  public void setReleaseDate(final LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }
  
  public LocalDate getReleaseDate() {
    return releaseDate;
  }
  
  public void setRevenue(final Long revenue) {
    this.revenue = revenue;
  }

  public Long getRevenue() {
    return revenue;
  }

  public void setRuntime(final Integer runtime) {
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
