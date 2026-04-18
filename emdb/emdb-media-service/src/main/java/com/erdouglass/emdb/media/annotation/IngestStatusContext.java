package com.erdouglass.emdb.media.annotation;

import jakarta.enterprise.context.RequestScoped;

import com.erdouglass.emdb.common.event.IngestStatusChanged;

@RequestScoped
public class IngestStatusContext {
  private IngestStatusChanged event;

  public void set(IngestStatusChanged event) {
    this.event = event; 
  }
  
  public IngestStatusChanged get() { 
    return event; 
  }
}
