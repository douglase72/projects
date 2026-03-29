package com.erdouglass.emdb.common.comand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.ShowConstants;

public record UpdateMovieCredit(
    @NotBlank @Size(max = ShowConstants.ROLE_MAX_LENGTH) String role,
    @PositiveOrZero Integer order) {
  
  public static UpdateMovieCredit of(String role, Integer order) {
    return new UpdateMovieCredit(role, order);
  }  

}
