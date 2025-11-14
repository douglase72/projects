package com.erdouglass.emdb.media.entity;

import java.util.Optional;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/// An abstract base class representing shared attributes of visual media content.
///
/// This class consolidates common properties found in both Movies and TV Shows,
/// such as the title (`name`), `overview`, `score`, `status`, and associated
/// imagery (`poster`, `backdrop`).
///
/// It extends {@link BasicEntity} to inherit identity management and auditing
/// timestamps.
@MappedSuperclass
public abstract class Show extends BasicEntity<Integer> {

  /// The path or URL to the backdrop image.
  @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH)
  private String backdrop;

  /// The URL of the show's official homepage.
  @Size(max = Configuration.URL_MAX_LENGTH)
  String homepage;

  /// The name or title of the show. Cannot be blank.
  @NotBlank
  @Size(max = ShowConstants.NAME_MAX_LENGTH)
  private String name;

  /// The ISO 639-1 code for the original language (e.g., "en").
  @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH)
  String originalLanguage;

  /// A short summary or plot overview of the show.
  @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH)
  @Column(length = ShowConstants.OVERVIEW_MAX_LENGTH)
  private String overview;

  /// The path or URL to the poster image.
  @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH)
  private String poster;

  /// The user score or rating, typically on a scale of 0 to 10.
  @Min(0)
  @Max(10)
  private Float score;

  /// The current production status of the show (e.g., RELEASED, CANCELED).
  /// Cannot be null.
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(length = ShowConstants.STATUS_MAX_LENGTH)
  private ShowStatus status;

  /// A short, catchy tagline for the show.
  @Size(max = ShowConstants.TAGLINE_MAX_LENGTH)
  private String tagline;

  protected Show() {
    super();
  }

  protected Show(Integer tmdbId, String name, ShowStatus status) {
    super(tmdbId);
    this.name = name;
    this.status = status;
  }

  public void backdrop(String backdrop) {
    this.backdrop = backdrop;
  }

  public Optional<String> backdrop() {
    return Optional.ofNullable(backdrop);
  }

  public void homepage(String homepage) {
    this.homepage = homepage;
  }

  public Optional<String> homepage() {
    return Optional.ofNullable(homepage);
  }

  public void name(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  public void originalLanguage(String originalLanguage) {
    this.originalLanguage = originalLanguage;
  }

  public Optional<String> originalLanguage() {
    return Optional.ofNullable(originalLanguage);
  }

  public void overview(String overview) {
    this.overview = overview;
  }

  public Optional<String> overview() {
    return Optional.ofNullable(overview);
  }

  public void poster(String poster) {
    this.poster = poster;
  }

  public Optional<String> poster() {
    return Optional.ofNullable(poster);
  }

  public void score(Float score) {
    this.score = score;
  }

  public Optional<Float> score() {
    return Optional.ofNullable(score);
  }

  public void status(ShowStatus status) {
    this.status = status;
  }

  public ShowStatus status() {
    return status;
  }

  public void tagline(String tagline) {
    this.tagline = tagline;
  }

  public Optional<String> tagline() {
    return Optional.ofNullable(tagline);
  }

}
