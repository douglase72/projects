package com.erdouglass.emdb.media.annotation;

import java.util.UUID;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class CorrelationContext {
  private UUID correlationId;
  
  public UUID getId() { 
    return correlationId; 
  }
  
  public void setId(UUID id) { 
    this.correlationId = id; 
  }
}
