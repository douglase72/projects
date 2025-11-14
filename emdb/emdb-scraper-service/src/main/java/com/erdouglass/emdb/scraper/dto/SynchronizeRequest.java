package com.erdouglass.emdb.scraper.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SynchronizeRequest(
    @NotNull @Positive Long emdbId, 
    @NotNull @Positive Integer tmdbId) {

}
