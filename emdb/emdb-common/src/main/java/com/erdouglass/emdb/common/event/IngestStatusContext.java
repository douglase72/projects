package com.erdouglass.emdb.common.event;

import java.util.UUID;

import jakarta.enterprise.context.RequestScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RequestScoped
public class IngestStatusContext {
  private IngestStatusChanged event;
  private UUID correlationId;
  
  public void setCorrelationId(UUID correlationId) {
    this.correlationId = correlationId;
  }

  public UUID getCorrelationId() {
    return correlationId;
  }  
  
  public void set(@NotNull @Valid IngestStatusChanged event) {
    this.event = event; 
  }
  
  public IngestStatusChanged get() { 
    return event; 
  }
}
