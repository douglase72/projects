package com.erdouglass.emdb.media;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record Image(    
    @NotNull UUID name,
    byte[] data) {}
