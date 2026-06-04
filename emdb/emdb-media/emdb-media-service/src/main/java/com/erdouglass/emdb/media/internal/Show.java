package com.erdouglass.emdb.media.internal;

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
import com.erdouglass.emdb.media.show.ShowConstants;
import com.erdouglass.emdb.media.show.ShowStatus;

@MappedSuperclass
public abstract class Show extends Media {

  @Column(unique = true)
  private UUID backdrop;
  
  @Size(max = Configuration.URL_MAX_LENGTH)
  private String homepage;
  
  @NotBlank
  @Column(name = "original_language")
  @Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH)
  private String originalLanguage;
  
  @Size(max = ShowConstants.OVERVIEW_MAX_LENGTH)
  private String overview;
  
  @Column(unique = true)
  private UUID poster;
  
  @NotNull
  @Min(0)
  @Max(10)
  private Float score;
  
  @NotNull
  @Enumerated(EnumType.STRING)
  @Column(length = ShowConstants.STATUS_MAX_LENGTH)
  private ShowStatus status;
  
  @Size(max = ShowConstants.TAGLINE_MAX_LENGTH)
  private String tagline;
  
  @NotBlank
  @Size(max = ShowConstants.TITLE_MAX_LENGTH)
  private String title;
  
  @Column(name="tmdb_backdrop", unique = true)
  @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH)
  private String tmdbBackdrop;
  
  @Column(name="tmdb_poster", unique = true)
  @Size(min = ShowConstants.POSTER_MIN_LENGTH, max = ShowConstants.POSTER_MAX_LENGTH)
  private String tmdbPoster;  
  
  protected Show() {
    super();
  }
  
  public Show(final int tmdbId) {
    super(tmdbId);
  }
  
  public void setBackdrop(final UUID backdrop) {
    this.backdrop = backdrop;
  }

  public UUID getBackdrop() {
    return backdrop;
  }
  
  public void setHomepage(final String homepage) {
    this.homepage = homepage;
  }

  public String getHomepage() {
    return homepage;
  }

  public void setOriginalLanguage(final String originalLanguage) {
    this.originalLanguage = originalLanguage;
  }

  public String getOriginalLanguage() {
    return originalLanguage;
  }
  
  public void setOverview(final String overview) {
    this.overview = overview;
  }

  public String getOverview() {
    return overview;
  }

  public void setPoster(final UUID poster) {
    this.poster = poster;
  }

  public UUID getPoster() {
    return poster;
  }
  
  public void setScore(final Float score) {
    this.score = score;
  }

  public Float getScore() {
    return score;
  }

  public void setStatus(final ShowStatus status) {
    this.status = status;
  }

  public ShowStatus getStatus() {
    return status;
  }

  public void setTagline(final String tagline) {
    this.tagline = tagline;
  }

  public String getTagline() {
    return tagline;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }
  
  public void setTmdbBackdrop(final String tmdbBackdrop) {
    this.tmdbBackdrop = tmdbBackdrop;
  }
  
  public String getTmdbBackdrop() {
    return tmdbBackdrop;
  }
  
  public void setTmdbPoster(final String tmdbPoster) {
    this.tmdbPoster = tmdbPoster;
  }
  
  public String getTmdbPoster() {
    return tmdbPoster;
  }
}
