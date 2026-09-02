package com.erdouglass.emdb.media.person.adapter.in.rest;

import java.util.Objects;
import java.util.UUID;

public record PersonResponse(UUID id, Long version, String status) { 
  
  public PersonResponse {
    Objects.requireNonNull(id, "person id is required");
    Objects.requireNonNull(version, "version is required");
    Objects.requireNonNull(status, "status is required");
  }
  
  public static PersonResponse of(UUID id, Long version, String status) {
    return new PersonResponse(id, version, status);
  }
}
