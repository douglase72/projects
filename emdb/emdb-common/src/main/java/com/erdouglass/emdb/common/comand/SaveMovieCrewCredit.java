package com.erdouglass.emdb.common.comand;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.ShowConstants;

public record SaveMovieCrewCredit(
    @Size(max = ShowConstants.ROLE_MAX_LENGTH) String job,
    @NotNull @Valid SavePerson person) {

  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private SavePerson person;
    private String job;
    
    private Builder() { }
    
    public SaveMovieCrewCredit build() {
      return new SaveMovieCrewCredit(job, person);
    }  
    
    public Builder person(SavePerson person) {
      this.person = person;
      return this;
    }
    
    public Builder job(String job) {
      this.job = job;
      return this;
    }
  }   
  
}
