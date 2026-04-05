package com.erdouglass.emdb.common.query;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.common.ShowConstants;

public record Role(
    @NotNull UUID id,
    @Size(max = ShowConstants.ROLE_MAX_LENGTH) String character,
    @NotNull @PositiveOrZero Integer episodeCount) {

}
