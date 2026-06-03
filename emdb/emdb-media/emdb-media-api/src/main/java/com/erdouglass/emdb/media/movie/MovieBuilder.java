package com.erdouglass.emdb.media.movie;

import java.time.LocalDate;

import com.erdouglass.emdb.media.show.ShowBuilder;

public abstract class MovieBuilder<T> extends ShowBuilder<T> {
  protected Integer budget;
  protected LocalDate releaseDate;
  protected Integer revenue;
  protected Integer runtime;
    
  public T budget(final Integer budget) {
    this.budget = budget;
    return self();
  }

  public T releaseDate(final LocalDate releaseDate) {
    this.releaseDate = releaseDate;
    return self();
  }
  
  public T revenue(final Integer revenue) {
    this.revenue = revenue;
    return self();
  }
  
  public T runtime(final Integer runtime) {
    this.runtime = runtime;
    return self();
  }
}
