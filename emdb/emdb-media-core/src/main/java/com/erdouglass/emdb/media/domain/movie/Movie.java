package com.erdouglass.emdb.media.domain.movie;

import java.util.Objects;

import com.erdouglass.emdb.media.SaveResult.Status;
import com.erdouglass.emdb.media.domain.shared.SourceId;

public class Movie {
  private MovieId id;
  private PublicId publicId;
  private SourceId sourceId;
  private Title title;
  private ReleaseDate releaseDate;
  private Status status;
  
  private Movie(Builder builder) {
    this.id = builder.id;
    this.publicId = builder.publicId;
    this.sourceId = builder.sourceId;
    this.title = builder.title;
    this.releaseDate = builder.releaseDate;
    this.status = builder.status;
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public MovieId id() {
    return id;
  }
  
  public PublicId publicId() {
    return publicId;
  }
  
  public ReleaseDate releaseDate() {
    return releaseDate;
  }
  
  public SourceId sourceId() {
    return sourceId;
  }
  
  public Status saveStatus() {
    return status;
  }
  
  public Title title() {
    return title;
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
        + ", title=" + title
        + ", relaseDate=" + releaseDate
        + "]";
  }
  
  public static final class Builder {
    private MovieId id;
    private PublicId publicId;
    private SourceId sourceId;
    private Title title;
    private ReleaseDate releaseDate;
    private Status status;
    
    private Builder() {}
    
    public Movie build() {
      Objects.requireNonNull(id, "id must not be null");
      Objects.requireNonNull(sourceId, "sourceId must not be null");
      Objects.requireNonNull(title, "title must not be null");
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
    
    public Builder releaseDate(ReleaseDate releaseDate) {
      this.releaseDate = releaseDate;
      return this;
    }
    
    public Builder sourceId(SourceId sourceId) {
      this.sourceId = sourceId;
      return this;
    }
    
    public Builder saveStatus(Status status) {
      this.status = status;
      return this;
    }
    
    public Builder title(Title title) {
      this.title = title;
      return this;
    }
  }
}
