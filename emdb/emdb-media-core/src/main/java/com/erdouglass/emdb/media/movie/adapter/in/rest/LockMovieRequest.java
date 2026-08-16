package com.erdouglass.emdb.media.movie.adapter.in.rest;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/// Request body for locking or unlocking a title.
///
/// The version is required even though a lock changes no details: locking is
/// still a write that bumps the version, and taking the lock without checking
/// staleness would let a client freeze a title it had not actually seen.
///
/// @param lock `true` to lock the title against detail changes, `false` to
///        release it
/// @param version the version the client last read
public record LockMovieRequest(@NotNull Boolean lock, @NotNull @PositiveOrZero Long version) {}
