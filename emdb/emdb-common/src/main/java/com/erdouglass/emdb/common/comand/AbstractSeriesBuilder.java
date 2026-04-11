package com.erdouglass.emdb.common.comand;

import java.time.LocalDate;

import com.erdouglass.emdb.common.SeriesType;

public abstract class AbstractSeriesBuilder<T> extends AbstractShowBuilder<T> {
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
