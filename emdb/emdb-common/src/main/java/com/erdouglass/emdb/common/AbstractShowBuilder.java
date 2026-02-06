package com.erdouglass.emdb.common;

public abstract class AbstractShowBuilder<T> {
  protected String homepage;
  protected String originalLanguage;
  protected String overview;  
  protected Float score;
  protected ShowStatus status;
  protected String tagline;
  protected String title;
  protected Integer tmdbId;

  public T homepage(String homepage) {
    this.homepage = homepage;
    return self();
  }

  public T overview(String overview) {
    this.overview = overview;
    return self();
  }

  public T originalLanguage(String originalLanguage) {
    this.originalLanguage =originalLanguage;
    return self();
  }

  public T score(Float score) {
    this.score = score;
    return self();
  }

  public T status(ShowStatus status) {
    this.status = status;
    return self();
  }

  public T tagline(String tagline) {
    this.tagline = tagline;
    return self();
  }
  
  public T title(String title) {
    this.title = title;
    return self();
  }

  public T tmdbId(Integer tmdbId) {
    this.tmdbId = tmdbId;
    return self();
  }

  protected abstract T self();
  
}