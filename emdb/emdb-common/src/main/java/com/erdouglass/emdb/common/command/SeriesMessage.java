package com.erdouglass.emdb.common.command;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record SeriesMessage(
    @NotNull @Valid SeriesCreateCommand series,
    @NotNull @Valid List<SeriesCreditCreateCommand> credits,
    @NotNull @Valid List<PersonCreateCommand> people) {
  
  @Override
  public String toString() {
    return "SeriesMessage[series=" + series 
        + ", credits=" + credits.size()
        + ", people=" + people.size()
        + "]";
  }

}
