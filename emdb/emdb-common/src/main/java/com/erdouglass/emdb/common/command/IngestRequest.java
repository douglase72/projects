package com.erdouglass.emdb.common.command;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IngestRequest(@NotNull @Positive Integer tmdbId) {

}
