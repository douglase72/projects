package com.erdouglass.emdb.media.command;

import java.time.LocalDate;

import com.erdouglass.emdb.media.SeriesType;
import com.erdouglass.emdb.media.show.ShowBuilder;

public abstract class SeriesBuilder<T> extends ShowBuilder<T> {
  protected LocalDate firstAirDate;
  protected LocalDate lastAirDate;
  protected SeriesType type;
  
  public T firstAirDate(LocalDate firstAirDate) {
    this.firstAirDate = firstAirDate;
    return self();
  }
  
  public T lastAirDate(LocalDate lastAirDate) {
    this.lastAirDate = lastAirDate;
    return self();
  }
  
  public T type(SeriesType type) {
    this.type = type;
    return self();
  }
}
