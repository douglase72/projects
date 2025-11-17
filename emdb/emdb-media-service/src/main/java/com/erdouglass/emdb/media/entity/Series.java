package com.erdouglass.emdb.media.entity;

import java.time.LocalDate;
import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.emdb.common.ShowStatus;
import com.erdouglass.validation.DateRange;

@Entity
@Table(name = "Series")
@SequenceGenerator(
    name = BasicEntity.SEQUENCE_GENERATOR, 
    sequenceName = "series_sequence", 
    initialValue = 1, 
    allocationSize = 1)
public class Series extends Show {
  
  @Column(name = "first_air_date")
  @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE)
  private LocalDate firstAirDate;
  
  @Size(max = ShowConstants.SERIES_TYPE_MAX_LENGTH) 
  private String type;

  Series() {
    
  }
  
  public Series(Integer tmdbId, String name, ShowStatus status) {
    super(tmdbId, name, status);
  }
  
  public void firstAirDate(LocalDate firstAirDate) {
    this.firstAirDate = firstAirDate;
  }

  public Optional<LocalDate> firstAirDate() {
    return Optional.ofNullable(firstAirDate);
  }
  
  public void type(String type) {
    this.type = type;
  }

  public Optional<String> type() {
    return Optional.ofNullable(type);
  }
  
  @Override
  public String toString() {
    return "Series[id=" + id() 
        + ", tmdbId=" + tmdbId() 
        + ", name=" + name() 
        + ", firstAirDate=" + firstAirDate
        + ", status=" + status() 
        + ", created=" + created() 
        + ", modified=" + modified() + "]";
  }
  
}
