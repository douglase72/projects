package com.erdouglass.emdb.common.query;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.ShowConstants;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record RoleDto(
    @NotNull UUID id,
    @Size(max = ShowConstants.ROLE_MAX_LENGTH) String role,
    @NotNull @PositiveOrZero Integer episodeCount) {

}
