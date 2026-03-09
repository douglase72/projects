package com.erdouglass.emdb.media.entity;

import java.time.LocalDate;
import java.util.UUID;

import com.erdouglass.emdb.common.ShowStatus;

public class Movie {
  
  private UUID backdrop;
  
  private Integer budget;
  
  private String homepage;
  
  private Long id;
  
  private String originalLanguage;
  
  private String overview;
  
  private UUID poster;
    
  private LocalDate releaseDate;
  
  private Integer revenue;
  
  private Integer runtime;
  
  private Float score;
  
  private ShowStatus status;
  
  private String tagline;
  
  private String title;
  
  private Integer tmdbId;
  
  public Movie() {
    id = 1L;
  }
  
  public void setBackdrop(UUID backdrop) {
    this.backdrop = backdrop;
  }
  
  public UUID getBackdrop() {
    return backdrop;
  }
  
  public void setBudget(Integer budget) {
    this.budget = budget;
  }  
  
  public Integer getBudget() {
    return budget;
  }  
  
  public void setHomepage(String homepage) {
    this.homepage = homepage;
  }
  
  public String getHomepage() {
    return homepage;
  }
  
  public Long getId() {
    return id;
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
  
  public void setReleaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }

  public LocalDate getReleaseDate() {
    return releaseDate;
  }
  
  public void setRevenue(Integer revenue) {
    this.revenue = revenue;
  }
  
  public Integer getRevenue() {
    return revenue;
  }
  
  public void setRuntime(Integer runtime) {
    this.runtime = runtime;
  }

  public Integer getRuntime() {
    return runtime;
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
  
  @Override
  public String toString() {
    return "Movie[id=" + id
        + ", tmdbId=" + tmdbId
        + ", title=" + title
        + ", relaseDate=" + releaseDate
        + ", score=" + score
        + ", status=" + status
        + ", runtime=" + runtime
        + ", budget=" + budget
        + ", revenue=" + revenue
        + ", backdrop=" + backdrop
        + ", poster=" + poster
        + "]";
  }

}
