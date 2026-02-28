package com.erdouglass.emdb.media.entity;

import java.util.Objects;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.ShowConstants;

@Entity
public class MovieCredit extends Credit {
  
  /// The @JoinColumn annotation maps the {@link Movie#id} primary key to the 
  /// foreign key in the Credits table. A {@code MovieCredit} can't exist 
  /// without a {@link Movie}.
  @ManyToOne
  @JoinColumn(name = "movie_id", updatable = false)
  private Movie movie;
  
  @Size(max = ShowConstants.ROLE_MAX_LENGTH)
  private String role;
  
  public MovieCredit() {
    
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
  
  public boolean isEqualTo(MovieCredit other) {
    return Objects.equals(role(), other.role())
        && Objects.equals(order(), other.order());
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
