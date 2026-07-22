package com.erdouglass.emdb.media.application.port.inbound;

/// Outcome of a successful edit: the echoed address and the version this
/// write minted. Opaque by contract — the increment is an ORM detail, not
/// something clients may extrapolate.
public record UpdateResult(String id, Long version) {}
