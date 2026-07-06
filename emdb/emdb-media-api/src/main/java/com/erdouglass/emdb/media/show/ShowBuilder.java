package com.erdouglass.emdb.media.show;

import java.math.BigDecimal;

import com.erdouglass.emdb.media.Image;

public abstract class ShowBuilder<T> {
  protected Image backdrop;
  protected String homepage;
  protected String originalLanguage;
  protected String overview; 
  protected Image poster;
  protected BigDecimal score;
  protected ShowStatus status;
  protected String tagline;
  protected String title;
  
  public T backdrop(final Image backdrop) {
    this.backdrop = backdrop;
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
  
  public T poster(final Image poster) {
    this.poster = poster;
    return self();
  }

  public T originalLanguage(final String originalLanguage) {
    this.originalLanguage =originalLanguage;
    return self();
  }

  public T score(final BigDecimal score) {
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