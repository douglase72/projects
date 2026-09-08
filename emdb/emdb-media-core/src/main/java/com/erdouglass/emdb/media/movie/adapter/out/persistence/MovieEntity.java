package com.erdouglass.emdb.media.movie.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;

import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Title;

@Entity
@Table(
    name = "movie",
    uniqueConstraints = {
      @UniqueConstraint(name = "uq_movie_tmdb_id", columnNames = { "tmdb_id" }),
    }
  )
class MovieEntity {
  
  @Id
  private UUID id;
  
  @Column(name = "tmdb_id", nullable = false, updatable = false)
  private Integer tmdbId;
  
  @Version
  private Long version;
  
  @Column(name = "original_language", length = LanguageCode.LENGTH)
  private String originalLanguage;
  
  @Column(length = Overview.MAX_LENGTH)
  private String overview;
  
  @Column(name = "release_date")
  private LocalDate releaseDate;
  
  @Column(name = "score", precision = 5, scale = 3)
  private BigDecimal score;
  
  @Column(nullable = false, length = Title.MAX_LENGTH)
  private String title;
  
  MovieEntity() { }
  
  public void setOriginalLanguage(String originalLanguage) { this.originalLanguage = originalLanguage; }
  public Optional<String> getOriginalLanguage() { return Optional.ofNullable(originalLanguage); }
  
  public void setOverview(String overview) { this.overview = overview; }
  public Optional<String> getOverview() { return Optional.ofNullable(overview); }
  
  public void setId(UUID id) { this.id = id; }
  public UUID getId() { return id; }
  
  public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
  public Optional<LocalDate> getReleaseDate() { return Optional.ofNullable(releaseDate); }
  
  public void setScore(BigDecimal score) { this.score = score; }
  public Optional<BigDecimal> getScore() { return Optional.ofNullable(score); }
  
  public void setTitle(String title) { this.title = title; }
  public String getTitle() { return title; }
  
  public void setTmdbId(int tmdbId) { this.tmdbId = tmdbId; }
  public Integer getTmdbId() { return tmdbId; }
  
  public void setVersion(long version) { this.version = version; }
  public Long getVersion() { return version; }
}
