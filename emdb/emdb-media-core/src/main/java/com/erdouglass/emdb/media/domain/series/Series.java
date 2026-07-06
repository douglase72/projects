package com.erdouglass.emdb.media.domain.series;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.SeriesType;
import com.erdouglass.emdb.media.domain.shared.Media;
import com.erdouglass.emdb.media.domain.shared.Show;
import com.erdouglass.emdb.media.show.ShowConstants;

@Entity
@Table(
    name = "series",
    uniqueConstraints = @UniqueConstraint(
      name = "uq_series_title_first_air_date",
      columnNames = { "title", "first_air_date" }
    )
  )
@SequenceGenerator(
    name = Media.SEQUENCE_GENERATOR, 
    sequenceName = "series_sequence", 
    initialValue = 1, 
    allocationSize = 1)
public class Series extends Show {

  @Column(name = "first_air_date")
  @DateRange(min = ShowConstants.SERIES_MIN_DATE, max = ShowConstants.MAX_DATE)
  private LocalDate firstAirDate;
  
  @Enumerated(EnumType.STRING)
  @Column(length = ShowConstants.SERIES_TYPE_MAX_LENGTH) 
  private SeriesType type;

  /// Default constructor required by JPA.
  Series() {}
  
  public Series(long externalId) {
    super(externalId);
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
        + ", tmdbId=" + getExternalId() 
        + ", title=" + getTitle() 
        + ", firstAirDate=" + firstAirDate 
        + "]";
  }    
}
