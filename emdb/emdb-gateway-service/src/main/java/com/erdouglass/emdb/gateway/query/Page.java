package com.erdouglass.emdb.gateway.query;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/// A page of results without total counts.
///
/// Designed for offset-based pagination using
/// [PageRequest#withoutTotal()] to avoid an expensive `SELECT COUNT(*)`
/// on large tables. Clients should iterate pages until [#hasNext()]
/// returns `false`.
///
/// @param content the results for this page
/// @param page the current 1-based page number
/// @param size the maximum number of results per page
/// @param hasNext `true` if more pages are available after this one
/// @param <T> the element type
public record Page<T>(
    @NotNull List<T> content,
    @NotNull @Positive Integer page,
    @NotNull @Positive Integer size,
    @NotNull Boolean hasNext) {}
