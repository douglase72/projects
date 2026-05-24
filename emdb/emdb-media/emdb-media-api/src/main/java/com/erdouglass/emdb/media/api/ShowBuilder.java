package com.erdouglass.emdb.media.api;

public abstract class ShowBuilder<T> {
  protected String tmdbBackdrop;
  protected String homepage;
  protected String originalLanguage;
  protected String overview; 
  protected String tmdbPoster;
  protected Float score;
  protected ShowStatus status;
  protected String tagline;
  protected String title;
  
  public T tmdbBackdrop(final String tmdbBackdrop) {
    this.tmdbBackdrop = tmdbBackdrop;
    return self();
  }

  public T homepage(final String homepage) {
    this.homepage = homepage;
    return self();
  }

  public T overview(final String overview) {
    this.overview = overview;
    return self();
  }
  
  public T tmdbPoster(final String tmdbPoster) {
    this.tmdbPoster = tmdbPoster;
    return self();
  }

  public T originalLanguage(final String originalLanguage) {
    this.originalLanguage =originalLanguage;
    return self();
  }

  public T score(final Float score) {
    this.score = score;
    return self();
  }
  
  public T status(final ShowStatus status) {
    this.status = status;
    return self();
  }  

  public T tagline(final String tagline) {
    this.tagline = tagline;
    return self();
  }
  
  public T title(final String title) {
    this.title = title;
    return self();
  }

  protected abstract T self();
}