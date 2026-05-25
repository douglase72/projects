package com.erdouglass.emdb.media.api;

import java.util.UUID;

public record Image(
    String tmdbName,
    UUID emdbName,
    byte[] data) {
  
  public static Builder builder() {
    return new Builder();
  }

  @Override
  public String toString() {
    return "Image[tmdbName=" + tmdbName
        + ", emdbName=" +emdbName
        + "]";
  }
  
  public static final class Builder {
    private String tmdbName;
    private UUID emdbName;
    private byte[] data;
    
    private Builder() {}
    
    public Image build() {
      return new Image(tmdbName, emdbName, data);
    }
    
    public Builder data(final byte[] data) {
      this.data = data;
      return this;
    }
    
    public Builder emdbName(final UUID emdbName) {
      this.emdbName = emdbName;
      return this;
    }
    
    public Builder tmdbName(final String tmdbName) {
      this.tmdbName = tmdbName;
      return this;
    }   
  }
}
