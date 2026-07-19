package com.erdouglass.emdb.media.domain.movie;

import java.util.Objects;
import java.util.Optional;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.domain.shared.OriginalLanguage;
import com.erdouglass.emdb.media.domain.shared.PublicId;
import com.erdouglass.emdb.media.domain.shared.SourceId;
import com.erdouglass.emdb.media.domain.shared.Title;
import com.erdouglass.emdb.media.domain.shared.Version;

/// Aggregate root for a movie in the media bounded context.
///
/// The consistency boundary of the write model: any rule spanning more than
/// one of this movie's fields is enforced here (single-field rules live in
/// the value objects, which arrive pre-validated). The class is pure Java by
/// design — no framework import may ever appear in this package; persistence
/// and transport shapes are the adapters' problem.
///
/// Identity: a Movie carries three identifiers with strictly separated jobs —
/// [MovieId] (internal surrogate, never leaves the hexagon)
/// [PublicId] (URL-facing, database-assigned)
/// [SourceId] (external provenance, the upsert key). 
/// Equality and hash are by [MovieId] alone: two snapshots of the same movie 
/// are the same movie, whatever their state.
public final class Movie {
  private final MovieId id;
  private PublicId publicId;
  private final SourceId sourceId;
  private Title title;
  private ReleaseDate releaseDate;
  private OriginalLanguage originalLanguage;
  private Version version;
  
  private Movie(Builder builder) {
    this.id = builder.id;
    this.publicId = builder.publicId;
    this.sourceId = builder.sourceId;
    this.title = builder.title;
    this.releaseDate = builder.releaseDate;
    this.originalLanguage = builder.originalLanguage;
    this.version = builder.version;
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public MovieId id() {
    return id;
  }
  
  public void merge(SaveMovieCommand command) {
    this.title = Title.of(command.title());
    this.releaseDate = ReleaseDate.of(command.releaseDate());
    this.originalLanguage = OriginalLanguage.of(command.originalLanguage());
  }
  
  public OriginalLanguage originalLanguage() {
    return originalLanguage;
  }
  
  public Optional<PublicId> publicId() {
    return Optional.ofNullable(publicId);
  }
  
  public Optional<ReleaseDate> releaseDate() {
    return Optional.ofNullable(releaseDate);
  }
  
  public SourceId sourceId() {
    return sourceId;
  }
  
  public Title title() {
    return title;
  }
  
  public Optional<Version> version() {
    return Optional.ofNullable(version);
  }
  
  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Movie other = (Movie) obj;
    return Objects.equals(id, other.id);
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[id=" + id
        + ", publicId=" + publicId
        + ", sourceId=" + sourceId
        + ", version=" + version
        + ", title=" + title
        + ", releaseDate=" + releaseDate
        + "]";
  }
  
  public static final class Builder {
    private MovieId id;
    private PublicId publicId;
    private SourceId sourceId;
    private Title title;
    private ReleaseDate releaseDate;
    private OriginalLanguage originalLanguage;
    private Version version;
    
    private Builder() {}
    
    public Movie build() {
      Objects.requireNonNull(id, "id must not be null");
      Objects.requireNonNull(sourceId, "sourceId must not be null");
      Objects.requireNonNull(title, "title must not be null");
      Objects.requireNonNull(originalLanguage, "originalLanguage must not be null");
      return new Movie(this);
    }
    
    public Builder id(MovieId id) {
      this.id = id;
      return this;
    }
        
    public Builder publicId(PublicId publicId) {
      this.publicId = publicId;
      return this;
    }
    
    public Builder originalLanguage(OriginalLanguage originalLanguage) {
      this.originalLanguage = originalLanguage;
      return this;
    }
    
    public Builder releaseDate(ReleaseDate releaseDate) {
      this.releaseDate = releaseDate;
      return this;
    }
    
    public Builder sourceId(SourceId sourceId) {
      this.sourceId = sourceId;
      return this;
    }
    
    public Builder title(Title title) {
      this.title = title;
      return this;
    }
    
    public Builder version(Version version) {
      this.version = version;
      return this;
    }
  }
}
