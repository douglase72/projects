package com.erdouglass.emdb.media.adapter.inbound.rest.movie;

import java.math.BigDecimal;
import java.util.Optional;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.Title;

public record UpdateMovieRequest(
    @NotNull @PositiveOrZero Long version, 
    @NotBlank @Size(max = Title.MAX_LENGTH) String title,
    Optional<String> releaseDate,
    Optional<@Min(0) @Max(10) BigDecimal> score,
    Optional<@Pattern(regexp = "[a-z]{2}") String> originalLanguage) {
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private Long version;
    private String title;
    private String releaseDate;
    private BigDecimal score;
    private String originalLanguage;
    
    private Builder() {}

    public UpdateMovieRequest build() {
      return new UpdateMovieRequest(
          version,
          title, 
          Optional.ofNullable(releaseDate), 
          Optional.ofNullable(score),
          Optional.ofNullable(originalLanguage));
    }
    
    public Builder originalLanguage(String originalLanguage) {
      this.originalLanguage =originalLanguage;
      return this;
    }
    
    public Builder releaseDate(String releaseDate) {
      this.releaseDate = releaseDate;
      return this;
    }
    
    public Builder score(BigDecimal score) {
      this.score = score;
      return this;
    }
    
    public Builder title(String title) {
      this.title = title;
      return this;
    }
    
    public Builder version(Long version) {
      this.version = version;
      return this;
    }
  }  
}
