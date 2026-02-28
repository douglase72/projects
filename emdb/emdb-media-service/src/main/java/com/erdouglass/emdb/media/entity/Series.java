package com.erdouglass.emdb.media.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.erdouglass.emdb.common.CreditType;
import com.erdouglass.emdb.common.SeriesType;
import com.erdouglass.emdb.common.ShowConstants;
import com.erdouglass.validation.DateRange;

@Entity
@Table(name = "Series")
@SequenceGenerator(
    name = BasicEntity.SEQUENCE_GENERATOR, 
    sequenceName = "series_sequence", 
    initialValue = 1, 
    allocationSize = 1)
public class Series extends Show {
  
  /// The credits collection in a series is a bidirectional association 
  /// specified by the mappedBy field which maps the {@link Series#id} 
  /// primary key to the foreign key in the Credits table.
  @OneToMany(mappedBy = _SeriesCredit.SERIES)
  private List<SeriesCredit> credits = new ArrayList<>();
  
  @Column(name = "first_air_date")
  @DateRange(min = ShowConstants.MOVIE_MIN_DATE, max = ShowConstants.MAX_DATE)
  private LocalDate firstAirDate;
  
  @Enumerated(EnumType.STRING)
  @Column(length = ShowConstants.SERIES_TYPE_MAX_LENGTH) 
  private SeriesType type;

  Series() {
    
  }
  
  public Series(String title) {
    super(title);
  }
  
  public Series(Integer tmdbId, String title) {
    super(tmdbId, title);
  }
  
  public List<SeriesCredit> cast() {
    return credits.stream().filter(c -> c.type() == CreditType.CAST).toList();
  }
  
  public void credits(List<SeriesCredit> credits) {
    this.credits = new ArrayList<>(credits);
  }
  
  public List<SeriesCredit> credits() {
    return List.copyOf(credits);
  }
  
  public List<SeriesCredit> crew() {
    return credits.stream().filter(c -> c.type() == CreditType.CREW).toList();
  }
  
  public void firstAirDate(LocalDate firstAirDate) {
    this.firstAirDate = firstAirDate;
  }

  public Optional<LocalDate> firstAirDate() {
    return Optional.ofNullable(firstAirDate);
  }
  
  public void type(SeriesType type) {
    this.type = type;
  }

  public Optional<SeriesType> type() {
    return Optional.ofNullable(type);
  }
  
  @Override
  public String toString() {
    return "Series[id=" + id() 
        + ", tmdbId=" + tmdbId() 
        + ", title=" + title() 
        + ", firstAirDate=" + firstAirDate
        + ", created=" + created() 
        + ", modified=" + modified() + "]";
  }
  
}
