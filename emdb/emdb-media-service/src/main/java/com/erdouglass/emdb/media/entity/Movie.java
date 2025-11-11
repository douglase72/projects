package com.erdouglass.emdb.media.entity;

import java.time.LocalDate;
import java.util.Optional;

import com.erdouglass.emdb.common.query.MovieDto;
import com.erdouglass.emdb.common.query.ShowStatus;
import com.erdouglass.validation.DateRange;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.PositiveOrZero;

/// Represents a Movie entity in the database.
///
/// This is a concrete entity that extends the {@link Show}
/// superclass, fixing the external ID type to {@code Long}.
///
/// It adds movie-specific fields like {@code budget},
/// {@code releaseDate}, {@code runtime}, and {@code revenue}.
///
/// This class defines the database table "Movies" and ensures
/// a unique constraint on the business key ({@code source} and
/// {@code external_id}). It also specifies its own sequence
/// generator ("movie_sequence") for its primary key.
@Entity
@Table(
    name = "Movies",
    uniqueConstraints = @UniqueConstraint(
    		name = "UQ_movie_source_external_id",
    		columnNames = {"source", "external_id"})
)
@SequenceGenerator(
    name = BasicEntity.SEQUENCE_GENERATOR, 
    sequenceName = "movie_sequence", 
    initialValue = 1, 
    allocationSize = 1)
public class Movie extends Show<Long> {
	
	/// The production budget of the movie, in dollars.
  /// Must be zero or positive.
	@PositiveOrZero 
	private Integer budget;

	/// The theatrical release date of the movie.
	@Column(name = "release_date")
	@DateRange(min = MovieDto.MIN_DATE, max = MovieDto.MAX_DATE) 
	private LocalDate releaseDate;
	
	/// The runtime of the movie, in minutes.
  /// Must be zero or positive.
	@PositiveOrZero 
	private Integer runtime;
	
	/// The box office revenue of the movie, in dollars.
  /// Must be zero or positive.
	@PositiveOrZero 
	private Integer revenue;
	
	Movie() {
		
	}
	
	public Movie(String source, Long externalId, String name, ShowStatus status) {
		super(source, externalId, name, status);
	}
	
  public void budget(Integer budget) {
    this.budget = budget;
  }

  public Optional<Integer> budget() {
    return Optional.ofNullable(budget);
  }
	
	public void releaseDate(LocalDate releaseDate) {
		this.releaseDate = releaseDate;
	}
	
	public Optional<LocalDate> releaseDate() {
		return Optional.ofNullable(releaseDate);
	}
	
  public void revenue(Integer revenue) {
    this.revenue = revenue;
  }

  public Optional<Integer> revenue() {
    return Optional.ofNullable(revenue);
  }
  
  public void runtime(Integer runtime) {
    this.runtime = runtime;
  }

  public Optional<Integer> runtime() {
    return Optional.ofNullable(runtime);
  }
  
  @Override
  public String toString() {
    return "Movie[id=" + id()
    	+ ", source=" + source()
    	+ ", externalId=" + externalId()
      + ", name=" + name()
      + ", releaseDate=" + releaseDate
      + ", status=" + status()
      + ", created=" + created()
      + ", modified=" + modified()
      + "]";
  }
	
}
