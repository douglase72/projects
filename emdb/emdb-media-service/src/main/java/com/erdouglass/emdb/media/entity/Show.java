package com.erdouglass.emdb.media.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.Configuration;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;

/// An abstract base class representing shared attributes of visual media content.
///
/// This class consolidates common properties found in both Movies and TV Shows,
/// such as the title, overview, score, status, and associated imagery. It also 
/// defines the `tmdbId` which serves as the unique external business identifier.
///
/// It extends [BasicEntity] to inherit surrogate identity management and
/// auditing timestamps. 
@MappedSuperclass
public abstract class Show extends BasicEntity<Long> {

  /// The path or URL to the backdrop image.
  @Column(unique = true)
  private UUID backdrop;
  
  @Column(name="tmdb_backdrop")
  @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH)
  private String tmdbBackdrop;

  /// The URL of the show's official homepage.
  @Size(max = Configuration.URL_MAX_LENGTH)
  String homepage;

  /// The ISO 639-1 code for the original language (e.g., "en").
  @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH)
  String originalLanguage;

  /// A short summary or plot overview of the show.
  @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH)
  @Column(length = ShowConstants.OVERVIEW_MAX_LENGTH)
  private String overview;

  /// The path or URL to the poster image.
  @Column(unique = true)
  private UUID poster;
  
  @Column(name="tmdb_poster")
  @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH)
  private String tmdbPoster;

  /// The user score or rating, typically on a scale of 0 to 10.
  @Min(0)
  @Max(10)
  private Float score;

  /// The current production status of the show (e.g., RELEASED, CANCELED). Cannot
  /// be null.
  @Enumerated(EnumType.STRING)
  @Column(length = ShowConstants.STATUS_MAX_LENGTH)
  private ShowStatus status;

  /// A short tagline for the show.
  @Size(max = ShowConstants.TAGLINE_MAX_LENGTH)
  private String tagline;

  /// The title of the show. Cannot be blank.
  @NotBlank
  @Size(max = ShowConstants.TITLE_MAX_LENGTH)
  private String title;
  
  @NotNull
  @Column(name = "tmdb_id", unique = true, updatable = false)
  private Integer tmdbId;  

  protected Show() {
    super();
  }

  public void setBackdrop(UUID backdrop) {
    this.backdrop = backdrop;
  }

  public UUID getBackdrop() {
    return backdrop;
  }
  
  public void setTmdbBackdrop(String tmdbBackdrop) {
    this.tmdbBackdrop = tmdbBackdrop;
  }
  
  public String getTmdbBackdrop() {
    return tmdbBackdrop;
  }

  public void setHomepage(String homepage) {
    this.homepage = homepage;
  }

  public String getHomepage() {
    return homepage;
  }

  public void setOriginalLanguage(String originalLanguage) {
    this.originalLanguage = originalLanguage;
  }

  public String getOriginalLanguage() {
    return originalLanguage;
  }

  public void setOverview(String overview) {
    this.overview = overview;
  }

  public String getOverview() {
    return overview;
  }

  public void setPoster(UUID poster) {
    this.poster = poster;
  }

  public UUID getPoster() {
    return poster;
  }
  
  public void setTmdbPoster(String tmdbPoster) {
    this.tmdbPoster = tmdbPoster;
  }
  
  public String getTmdbPoster() {
    return tmdbPoster;
  }

  public void setScore(Float score) {
    this.score = score;
  }

  public Float getScore() {
    return score;
  }

  public void setStatus(ShowStatus status) {
    this.status = status;
  }

  public ShowStatus getStatus() {
    return status;
  }

  public void setTagline(String tagline) {
    this.tagline = tagline;
  }

  public String getTagline() {
    return tagline;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }
  
  public void setTmdbId(Integer tmdbId) {
    this.tmdbId = tmdbId;
  }
  
  public Integer getTmdbId() {
    return tmdbId;
  }

}
