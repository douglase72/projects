package com.erdouglass.emdb.scraper.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record IngestRequest(@NotNull @Positive Integer tmdbId) {

}
