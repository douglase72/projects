package com.erdouglass.emdb.common.comand;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateSeriesCredit(
    @NotNull @PositiveOrZero Integer totalEpisodes,
    @PositiveOrZero Integer order) {
  
  public static UpdateSeriesCredit of(Integer totalEpisodes) {
    return new UpdateSeriesCredit(totalEpisodes, null);
  }   
  
  public static UpdateSeriesCredit of(Integer totalEpisodes, Integer order) {
    return new UpdateSeriesCredit(totalEpisodes, order);
  }  

}
