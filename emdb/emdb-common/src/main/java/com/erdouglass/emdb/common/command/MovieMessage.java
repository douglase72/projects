package com.erdouglass.emdb.common.command;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record MovieMessage(
  @NotNull @Valid MovieCreateCommand movie,
  @NotNull @Valid List<MovieCreditCreateCommand> credits, 
  @NotNull @Valid List<PersonCreateCommand> people) {

  @Override
  public String toString() {
    return "MovieMessage[movie=" + movie 
        + ", credits=" + credits.size() 
        + ", people=" + people.size() 
        + "]";
  }

}
