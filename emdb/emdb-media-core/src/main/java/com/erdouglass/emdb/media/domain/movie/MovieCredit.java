package com.erdouglass.emdb.media.domain.movie;

import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.domain.shared.Credit;
import com.erdouglass.emdb.media.show.ShowConstants;

@Entity
public class MovieCredit extends Credit {
  
  /// The external id provides a stable immutable natural key.
  @NotNull
  @Column(name = "external_id", unique = true, updatable = false)
  private String externalId;
  
  @NotNull
  @Column(name = "movie_id", nullable = false, updatable = false)
  private Long movieId;
  
  /// The person's role on the film: character name for cast, job title for crew.
  @Size(max = ShowConstants.ROLE_MAX_LENGTH)
  private String role;

  MovieCredit() {}
  
  public MovieCredit(String externalId) {
    this.externalId = externalId;
  }
  
  public String getExternalId() {
    return externalId;
  }
  
  public void setMovieId(Long movieId) {
    this.movieId = movieId;
  }
  
  public Long getMovieId() {
    return movieId;
  }
  
  public void setRole(String role) {
    this.role = role;
  }

  public String getRole() {
    return role;
  }  
  
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass()) 
      return false;
    MovieCredit other = (MovieCredit) obj;
    return Objects.equals(getExternalId(), other.getExternalId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getExternalId());
  }
}
