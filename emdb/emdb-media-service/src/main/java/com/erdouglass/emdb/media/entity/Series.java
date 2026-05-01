package com.erdouglass.emdb.media.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.erdouglass.emdb.common.SeriesType;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.validation.DateRange;

@Entity
@Table(name = "Series")
@SequenceGenerator(
  name = SequenceEntity.SEQUENCE_GENERATOR, 
  sequenceName = "series_sequence", 
  initialValue = 1, 
  allocationSize = 1)
public non-sealed class Series extends Show {

  @Column(name = "first_air_date")
  @DateRange(min = ShowConstants.SERIES_MIN_DATE, max = ShowConstants.MAX_DATE)
  private LocalDate firstAirDate;
  
  @Enumerated(EnumType.STRING)
  @Column(length = ShowConstants.SERIES_TYPE_MAX_LENGTH) 
  private SeriesType type;
  
  public Series() {
    
  }
  
  public void setFirstAirDate(LocalDate firstAirDate) {
    this.firstAirDate = firstAirDate;
  }

  public LocalDate getFirstAirDate() {
    return firstAirDate;
  }
  
  public void setType(SeriesType type) {
    this.type = type;
  }

  public SeriesType getType() {
    return type;
  }
  
  @Override
  public String toString() {
    return "Series[id=" + getId() 
        + ", tmdbId=" + getTmdbId() 
        + ", title=" + getTitle() 
        + ", firstAirDate=" + firstAirDate 
        + ", poster=" + getPoster()
        + "]";
  }
}
