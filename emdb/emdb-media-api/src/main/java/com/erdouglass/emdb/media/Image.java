package com.erdouglass.emdb.media;

import java.util.Objects;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record Image(    
    @NotNull UUID name,
    byte[] data) {

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Image other = (Image) obj;
    return Objects.equals(name, other.name);
  }
  
  @Override
  public String toString() {
    return name.toString();
  }
}
