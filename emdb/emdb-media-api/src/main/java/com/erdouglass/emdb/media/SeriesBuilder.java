package com.erdouglass.emdb.media;

import java.time.LocalDate;

import com.erdouglass.emdb.media.SaveSeries.Type;
import com.erdouglass.emdb.media.show.ShowBuilder;

public abstract class SeriesBuilder<T> extends ShowBuilder<T> {
  protected LocalDate firstAirDate;
  protected LocalDate lastAirDate;
  protected Type type;
  
  public T firstAirDate(LocalDate firstAirDate) {
    this.firstAirDate = firstAirDate;
    return self();
  }
  
  public T lastAirDate(LocalDate lastAirDate) {
    this.lastAirDate = lastAirDate;
    return self();
  }
  
  public T type(Type type) {
    this.type = type;
    return self();
  }
}
