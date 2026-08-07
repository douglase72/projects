package com.erdouglass.emdb.media;

import java.util.Objects;

public record SavePersonCommand(
    SourceId sourceId,
    Name name,
    BirthDate birthDate,
    Gender gender) {

  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private SourceId sourceId;
    private Name name;
    private BirthDate birthDate;
    private Gender gender;
    
    private Builder() {}

    public SavePersonCommand build() {
      Objects.requireNonNull(sourceId, "sourceId must not be null");
      Objects.requireNonNull(name, "name must not be null");
      Objects.requireNonNull(gender, "gender must not be null");
      return new SavePersonCommand(
          sourceId,
          name, 
          birthDate, 
          gender);
    }
    
    public Builder birthDate(BirthDate birthDate) {
      this.birthDate = birthDate;
      return this;
    }
    
    public Builder gender(Gender gender) {
      this.gender = gender;
      return this;
    }
    
    public Builder name(Name name) {
      this.name = name;
      return this;
    }
    
    public Builder sourceId(SourceId sourceId) {
      this.sourceId = sourceId;
      return this;
    }
  }
}
