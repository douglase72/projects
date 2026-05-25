package com.erdouglass.emdb.media.domain;

import java.util.Optional;

import com.erdouglass.emdb.media.api.Image;

public record ImageUpdate(Image image, Optional<Image> toDelete) {}
