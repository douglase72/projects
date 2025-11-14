package com.erdouglass.emdb.common;

import java.time.LocalDate;

public abstract class AbstractMovieBuilder<T> extends AbstractShowBuilder<T> {
  protected Integer budget;
  protected String homepage;
  protected String originalLanguage;
  protected LocalDate releaseDate;
  protected Integer revenue;
  protected Integer runtime;
  protected String title;
  
  protected AbstractMovieBuilder() {

  }
  
  public T budget(Integer budget) {
    this.budget = budget;
    return self();
  }
  
  public T homepage(String homepage) {
    this.homepage = homepage;
    return self();
  }
  
  public T originalLanguage(String originalLanguage) {
    this.originalLanguage = originalLanguage;
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
  
  public T title(String title) {
    this.title = title;
    return self();
  }
  
}
