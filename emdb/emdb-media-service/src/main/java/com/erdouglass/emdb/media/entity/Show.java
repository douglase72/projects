package com.erdouglass.emdb.media.entity;

import java.util.Optional;

import com.erdouglass.emdb.common.configuration.Configuration;
import com.erdouglass.emdb.common.query.ShowDto;
import com.erdouglass.emdb.common.query.ShowStatus;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/// A mapped superclass that provides common fields for "show-like" entities.
///
/// It extends `BasicEntity` (specifying `Integer` for the `tmdbId`) and is
/// intended to be extended by concrete entities like `Movie` and `Series`.
///
/// This class includes properties shared by all "show" types, such as:
/// * `name` (title)
/// * `overview`
/// * `score`
/// * `poster` / `backdrop` paths
/// * `status` (e.g., RELEASED, IN_PRODUCTION)
/// * `tagline`
///
/// ## Design Pattern
///
/// A key feature of this class is the use of `Optional`-wrapping getters
/// (e.g., `overview()`, `tagline()`). This provides a null-safe, functional
/// way for the service layer to interact with entity data without risking
/// `NullPointerException`s.
///
/// @see com.erdouglass.emdb.media.entity.Movie
/// @see com.erdouglass.emdb.media.entity.BasicEntity
@MappedSuperclass
public abstract class Show extends BasicEntity<Integer> {

	@Size(min = ShowDto.POSTER_MIN_LENGTH, max = ShowDto.POSTER_MAX_LENGTH)
  private String backdrop;
	
	@Size(max = Configuration.URL_MAX_LENGTH) 
	String homepage;
	
	@NotBlank
	@Size(max = ShowDto.NAME_MAX_LENGTH)
	private String name;
	
	@Size(min = Configuration.ISO_639_1_LENGTH, max = Configuration.ISO_639_1_LENGTH) 
	String originalLanguage;
	
  @Size(max = ShowDto.OVERVIEW_MAX_LENGTH)
  @Column(length = ShowDto.OVERVIEW_MAX_LENGTH)
  private String overview;
  
  @Size(min = ShowDto.POSTER_MIN_LENGTH, max = ShowDto.POSTER_MAX_LENGTH)
  private String poster;
  
  @Min(0) @Max(10)
  private Float score;
  
	@NotNull
  @Enumerated(EnumType.STRING)
  @Column(length = ShowDto.STATUS_MAX_LENGTH)
	private ShowStatus status;
	
  @Size(max = ShowDto.TAGLINE_MAX_LENGTH)
  private String tagline;
  
  protected Show() {
  	super();
  }
  
  protected Show(String name, Integer tmdbId, ShowStatus status) {
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
