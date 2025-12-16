package com.erdouglass.emdb.common;

/// An abstract base class for builders that create "show" related DTOs,
/// such as movies or TV series. It follows the DRY principle by providing
/// common fields and fluent builder methods.
///
/// This class uses the "curiously recurring generic pattern" where `T` is the
/// type of the concrete subclass. This enables a fluent interface across the
/// inheritance hierarchy.
///
/// @param <T> The concrete builder type that extends this class.
public abstract class AbstractShowBuilder<T> {
  protected String backdrop;
  protected String homepage;
  protected String originalLanguage;
  protected String overview;
  protected String poster;
  protected Float score;
  protected ShowStatus status;
  protected String tagline;
  protected Integer tmdbId;

  protected AbstractShowBuilder() {

  }

  public T backdrop(String backdrop) {
    this.backdrop = backdrop;
    return self();
  }

  public T homepage(String homepage) {
    this.homepage = homepage;
    return self();
  }

  public T overview(String overview) {
    this.overview = overview;
    return self();
  }

  public T originalLanguage(String originalLanguage) {
    this.originalLanguage = originalLanguage;
    return self();
  }

  public T poster(String poster) {
    this.poster = poster;
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

  public T tmdbId(Integer tmdbId) {
    this.tmdbId = tmdbId;
    return self();
  }

  protected abstract T self();
}
