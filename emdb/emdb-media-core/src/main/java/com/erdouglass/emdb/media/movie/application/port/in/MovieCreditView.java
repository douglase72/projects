package com.erdouglass.emdb.media.movie.application.port.in;

import java.util.UUID;

public record MovieCreditView(
    UUID id,
    CreditType creditType,
    Long personId,
    String name,
    String role,
    Integer order,
    String department) { }
