package com.erdouglass.emdb.media.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.ShowConstants;

@Entity
@Table(name = "Movie_Credits")
public class MovieCredit extends Credit {
  
  /// The @JoinColumn annotation maps the {@link Series#id} primary key to the 
  /// foreign key in the Credits table. A {@code MovieCredit} can't exist 
  /// without a {@link Movie}.
  @ManyToOne
  @JoinColumn(name = "movie_id", updatable = false, nullable = false)
  private Movie movie;
  
  @Size(max = ShowConstants.ROLE_MAX_LENGTH)
  private String role;
  
  MovieCredit() {
    
  }
  
  public MovieCredit(String tmdbId) {
    super(tmdbId);
  }
  
  public void movie(Movie movie) {
    this.movie = movie;
  }

  public Movie movie() {
    return movie;
  }

  public void role(String role) {
    this.role = role;
  }

  public String role() {
    return role;
  }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id()
      + ", type=" + type()
      + ", role=" + role()
      + ", order=" + order().orElse(null)
      + "]";
  }
  
}
