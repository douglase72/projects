package com.erdouglass.emdb.media.adapter.inbound.rest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.OriginalLanguage;
import com.erdouglass.emdb.media.Title;

public record SaveMovieRequest(
    @NotBlank String source,
    @NotBlank String sourceId,
    @NotBlank @Size(max = Title.MAX_LENGTH) String title,
    String releaseDate,
    @NotBlank @Size(min = OriginalLanguage.LENGTH, max = OriginalLanguage.LENGTH) String originalLanguage) {

  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private String source;
    private String sourceId;
    private String releaseDate;
    private String title;
    private String originalLanguage;
    
    private Builder() {}

    public SaveMovieRequest build() {
      return new SaveMovieRequest(
          source,
          sourceId, 
          title, 
          releaseDate, 
          originalLanguage);
    }
    
    public Builder originalLanguage(final String originalLanguage) {
      this.originalLanguage =originalLanguage;
      return this;
    }
    
    public Builder sourceId(String source, String id) {
      this.source = source;
      this.sourceId = id;
      return this;
    }
    
    public Builder releaseDate(String releaseDate) {
      this.releaseDate = releaseDate;
      return this;
    }
    
    public Builder title(String title) {
      this.title = title;
      return this;
    }
  }
}
