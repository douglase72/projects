package com.erdouglass.emdb.common.comand;

import java.time.LocalDate;

public abstract class AbstractMovieBuilder<T> extends AbstractShowBuilder<T> {
  protected Integer budget;
  protected LocalDate releaseDate;
  protected Integer revenue;
  protected Integer runtime;
    
  public T budget(Integer budget) {
    this.budget = budget;
    return self();
  }

  public T releaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
    return self();
  }
  
  public T revenue(Integer revenue) {
    this.revenue = revenue;
    return self();
  }
  
  public T runtime(Integer runtime) {
    this.runtime = runtime;
    return self();
  }
}