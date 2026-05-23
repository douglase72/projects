package com.erdouglass.emdb.ingest.scraper.series;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

record Series(
    @NotNull @Positive Integer id,
    @NotBlank String name) {

}