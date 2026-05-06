package com.erdouglass.emdb.media.annotation;

import java.time.Duration;
import java.util.UUID;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class IngestContext {
  private UUID correlationId;
  private Duration persistDuration;
  
  public void setCorrelationId(UUID id) { 
    this.correlationId = id; 
  }
  
  public UUID getCorrelationId() { 
    return correlationId; 
  }
  
  public void setPersistDuration(Duration duration) {
    this.persistDuration = duration;
  }
  
  public Duration getPersistDuration() {
    return persistDuration;
  } 
}
