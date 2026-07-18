package com.erdouglass.emdb.media.adapter.outbound.movie;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.Generated;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.MediaConstants;

/// Persistence model of a movie row. Emphatically *not* the domain [Movie].
///
/// This class belongs to the adapter and is shaped by the database, not the
/// ubiquitous language: flat columns, mutable, no behavior. The separation is
/// what keeps JPA annotations, no-arg constructors, and generated values out
/// of the domain model — [MovieMapper] pays the translation toll on purpose.
/// Constraint annotations here are a schema-level backstop; the authoritative
/// rules already ran in the value objects.
@Entity 
@Table(name = "movie")
class MovieEntity {

  /// Internal surrogate key, application-generated ([MovieId] on the domain
  /// side). Never exposed publicly.
  @Id
  private UUID id;
  
  /// URL-friendly public identity, assigned by a database sequence — hence
  /// read-only here and re-selected after insert so the aggregate can carry
  /// it back out ([PublicId] on the domain side).
  @Generated
  @Column(name = "public_id", nullable = false, unique = true, updatable = false)
  private Long publicId;
  
  @Column(name = "release_date")
  @DateRange(min = MediaConstants.MOVIE_MIN_DATE, max = MediaConstants.MAX_DATE)
  private LocalDate releaseDate;
  
  /// External provenance pair ([SourceId] on the domain side), stored as the
  /// canonical lowercase token plus provider id. Guarded by the
  /// `uq_movie_source` unique constraint — the enforcement point for the
  /// cross-aggregate rule that no two movies share an external identity.
  @Column(nullable = false, length = 16)
  private String source;
  
  @Column(name = "source_id", nullable = false, length = 64)
  private String sourceId;
  
  @NotBlank
  @Size(max = MediaConstants.TITLE_MAX_LENGTH)
  private String title;
  
  MovieEntity() {}
  
  public void setId(UUID id) {
    this.id = id;
  }
  
  public UUID getId() {
    return id;
  }
  
  public Long getPublicId() {
    return publicId;
  }
  
  public void setReleaseDate(LocalDate releaseDate) {
    this.releaseDate = releaseDate;
  }
  
  public LocalDate getReleaseDate() {
    return releaseDate;
  }
  
  public void setSource(String source) {
    this.source = source;
  }
  
  public String getSource() {
    return source;
  }
  
  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }
  
  public String getSourceId() {
    return sourceId;
  }
  
  public void setTitle(String title) {
    this.title = title;
  }

  public String getTitle() {
    return title;
  }    
  
  @Override
  public String toString() {
    return "Movie[id=" + getId()
        + ", publicId=" + getPublicId()
        + ", source=" + getSource()
        + ", sourceId=" + getSourceId()
        + ", title=" + getTitle() 
        + ", releaseDate=" + getReleaseDate()
        + "]";
  }
}
