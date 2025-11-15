package com.erdouglass.emdb.common;

public abstract class AbstractSeriesBuilder<T> extends AbstractShowBuilder<T> {
  protected String name;
  protected String type;
  
  protected AbstractSeriesBuilder() {

  }
  
  public T name(String name) {
    this.name = name;
    return self();
  }
  
  public T type(String type) {
    this.type = type;
    return self();
  }
  
}
