package com.erdouglass.emdb.media;

import java.time.LocalDate;

import com.erdouglass.emdb.media.show.ShowBuilder;

public abstract class MovieBuilder<T> extends ShowBuilder<T> {
  protected Long budget;
  protected LocalDate releaseDate;
  protected Long revenue;
  protected Integer runtime;
    
  public T budget(final Long budget) {
    this.budget = budget;
    return self();
  }

  public T releaseDate(final LocalDate releaseDate) {
    this.releaseDate = releaseDate;
    return self();
  }
  
  public T revenue(final Long revenue) {
    this.revenue = revenue;
    return self();
  }
  
  public T runtime(final Integer runtime) {
    this.runtime = runtime;
    return self();
  }
}
