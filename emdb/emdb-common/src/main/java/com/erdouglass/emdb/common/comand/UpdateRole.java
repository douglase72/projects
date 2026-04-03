package com.erdouglass.emdb.common.comand;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.ShowConstants;

public record UpdateRole(
    @NotBlank @Size(max = ShowConstants.ROLE_MAX_LENGTH) String role,
    @NotNull @PositiveOrZero Integer episodeCount) {

  public static UpdateRole of(String role, Integer episodeCount) {
    return new UpdateRole(role, episodeCount);
  }
  
}
