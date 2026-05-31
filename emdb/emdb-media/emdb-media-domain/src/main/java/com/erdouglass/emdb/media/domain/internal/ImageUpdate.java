package com.erdouglass.emdb.media.domain.internal;

import java.util.Optional;

import com.erdouglass.emdb.media.Image;

public record ImageUpdate(Image image, Optional<Image> toDelete) {}
