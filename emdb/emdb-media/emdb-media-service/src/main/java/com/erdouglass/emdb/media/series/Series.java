package com.erdouglass.emdb.media.series;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import com.erdouglass.common.validation.DateRange;
import com.erdouglass.emdb.media.internal.Media;
import com.erdouglass.emdb.media.internal.Show;
import com.erdouglass.emdb.media.show.SeriesType;
import com.erdouglass.emdb.media.show.ShowConstants;

@Entity
@Table(
    name = "Series",
    uniqueConstraints = @UniqueConstraint(
      name = "uk_series_title_first_air_date",
      columnNames = { "title", "first_air_date" }
    )
  )
@SequenceGenerator(
    name = Media.SEQUENCE_GENERATOR, 
    sequenceName = "series_sequence", 
    initialValue = 1, 
    allocationSize = 1)
public class Series extends Show {
  
  /// The credits collection in a series is a bidirectional association 
  /// specified by the mappedBy field which maps the [Series#id] 
  /// primary key to the foreign key in the Credits table.
  @OneToMany(mappedBy = _SeriesCredit.SERIES)
  private List<SeriesCredit> credits = new ArrayList<>();

  @Column(name = "first_air_date")
  @DateRange(min = ShowConstants.SERIES_MIN_DATE, max = ShowConstants.MAX_DATE)
  private LocalDate firstAirDate;
  
  @Enumerated(EnumType.STRING)
  @Column(length = ShowConstants.SERIES_TYPE_MAX_LENGTH) 
  private SeriesType type;

  /// Default constructor required by JPA.
  protected Series() {}
  
  protected Series(final int tmdbId) {
    super(tmdbId);
  }
  
  public void setCredits(List<SeriesCredit> credits) {
    this.credits = new ArrayList<>(credits);
  }
  
  public List<SeriesCredit> getCredits() {
    return List.copyOf(credits);
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
