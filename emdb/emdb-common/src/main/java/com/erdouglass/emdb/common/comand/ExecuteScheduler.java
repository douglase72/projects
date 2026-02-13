package com.erdouglass.emdb.common.comand;

import jakarta.validation.constraints.NotNull;

public record ExecuteScheduler(@NotNull SchedulerType type) {
  
  public enum SchedulerType {
    MOVIES,
    SERIES,
    PEOPLE
  }

}
