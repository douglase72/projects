package com.erdouglass.emdb.media.application.port.inbound;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/// Outcome of a successful edit: the echoed address and the version this
/// write minted. Opaque by contract — the increment is an ORM detail, not
/// something clients may extrapolate.
public record UpdateResult(@NotBlank String id, @NotNull @PositiveOrZero Long version) {}
