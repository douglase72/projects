package com.erdouglass.emdb.media.movie;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.Hibernate;

import com.erdouglass.emdb.media.credit.Credit;
import com.erdouglass.emdb.media.show.ShowConstants;

/// A [Credit] on a [Movie]. Adds the movie association and the person's role on
/// the film — the character name for cast, or the job title for crew.
@Entity
class MovieCredit extends Credit {
  
  /// The @JoinColumn annotation maps the {@link Movie#id} primary key to the 
  /// foreign key in the Credits table. A {@code MovieCredit} can't exist 
  /// without a {@link Movie}.
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "movie_id", updatable = false)
  private Movie movie;
  
  /// The person's role on the film: character name for cast, job title for crew.
  @Size(max = ShowConstants.ROLE_MAX_LENGTH)
  private String role;
  
  /// The TMDB id provides a stable immutable natural key.
  @NotNull
  @Column(name = "tmdb_id", unique = true, updatable = false)
  private String tmdbId;
  
  protected MovieCredit() {}
  
  protected MovieCredit(final String tmdbId) {
    this.tmdbId = tmdbId;
  }

  public void setMovie(Movie movie) {
    this.movie = movie;
  }

  public Movie getMovie() {
    return movie;
  }

  public void setRole(String role) {
    this.role = role;
  }

  public String getRole() {
    return role;
  }
  
  public String getTmdbId() {
    return tmdbId;
  }
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || Hibernate.getClass(this) != Hibernate.getClass(obj))
      return false;
    MovieCredit other = (MovieCredit) obj;
    return Objects.equals(getTmdbId(), other.getTmdbId());
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(getTmdbId());
  }
  
  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + getId()
      + ", tmdb=" + getTmdbId()
      + ", type=" + getType()
      + ", role=" + getRole()
      + ", order=" + getOrder()
      + "]";
  }
}
