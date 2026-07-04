package com.erdouglass.emdb.media.person;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PersonCreditCreated(@NotNull @Positive Integer tmdbId) {}
