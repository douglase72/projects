package com.erdouglass.emdb.media.application.port.outbound.movie;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import com.erdouglass.emdb.media.domain.movie.FieldChange;
import com.erdouglass.emdb.media.domain.movie.MovieId;
import com.erdouglass.emdb.media.domain.movie.MoviePublicId;

public interface MovieAuditRepository {

  void append(@NotNull MovieId id, @NotNull MoviePublicId publicId, @NotNull List<FieldChange> changes);
}
