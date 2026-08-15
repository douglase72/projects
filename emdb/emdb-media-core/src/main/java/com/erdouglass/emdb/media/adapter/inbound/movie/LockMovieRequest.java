package com.erdouglass.emdb.media.adapter.inbound.movie;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record LockMovieRequest(@NotNull Boolean lock, @NotNull @PositiveOrZero Long version) {}
