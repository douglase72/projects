package com.erdouglass.emdb.media.image;

import java.util.Optional;

import com.erdouglass.emdb.media.Image;

public record ImageUpdate(Image image, Optional<Image> toDelete) {}
