package com.erdouglass.emdb.media.application.port.outbound;

import com.erdouglass.emdb.media.SaveResult.Status;
import com.erdouglass.emdb.media.domain.movie.Movie;

public record SaveStatus(Movie movie, Status status) {}
