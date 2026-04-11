package com.erdouglass.emdb.gateway.query;

import java.util.List;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/// A page of results without total counts.
///
/// Designed for offset-based pagination where clients iterate through pages until 
/// [#hasNext()] returns `false`.
///
/// @param results the results for this page
/// @param page the current 1-based page number
/// @param size the maximum number of results per page
/// @param hasNext `true` if more pages are available after this one
/// @param <T> the element type
public record Page<T>(
    @NotNull List<T> results,
    @NotNull @Positive Integer page,
    @NotNull @Positive Integer size,
    @NotNull Boolean hasNext) {}
