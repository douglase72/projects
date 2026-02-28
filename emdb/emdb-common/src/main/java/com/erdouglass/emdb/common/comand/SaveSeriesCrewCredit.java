package com.erdouglass.emdb.common.comand;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.ShowConstants;

public record SaveSeriesCrewCredit(
    @NotNull @Valid SavePerson person,
    @NotEmpty List<@Valid Job> jobs) {
  
  public record Job(
      @Size(max = ShowConstants.ROLE_MAX_LENGTH) String title,
      @NotNull @PositiveOrZero Integer episodeCount) {
    
    public static Job of(String title, Integer episodeCount) {
      return new Job(title, episodeCount);
    }  
  }
  
  public static Builder builder() {
    return new Builder();
  }
  
  public static final class Builder {
    private SavePerson person;
    private List<Job> jobs = new ArrayList<>();

    private Builder() {
    }

    public SaveSeriesCrewCredit build() {
      return new SaveSeriesCrewCredit(person, jobs);
    }

    public Builder person(SavePerson person) {
      this.person = person;
      return this;
    }
    
    public Builder job(String title, Integer episodeCount) {
      this.jobs.add(new Job(title, episodeCount));
      return this;
    }

    public Builder jobs(List<Job> jobs) {
      this.jobs = new ArrayList<>(jobs);
      return this;
    }
  }  

}
