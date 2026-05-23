package com.erdouglass.emdb.ingest.scraper.person;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

record Person(
    @NotNull @Positive Integer id,
    @NotBlank String name) {

}