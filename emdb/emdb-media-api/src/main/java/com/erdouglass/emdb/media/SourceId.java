package com.erdouglass.emdb.media;

import jakarta.validation.constraints.NotBlank;

public record SourceId(
    @NotBlank String source, 
    @NotBlank String id) {}
