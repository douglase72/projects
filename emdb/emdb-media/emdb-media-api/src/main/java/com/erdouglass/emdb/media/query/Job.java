package com.erdouglass.emdb.media.query;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import com.erdouglass.emdb.media.show.ShowConstants;

public record Job(
    @NotNull UUID creditId,
    @Size(max = ShowConstants.ROLE_MAX_LENGTH) String title,
    @NotNull @PositiveOrZero Integer episodeCount) {}
