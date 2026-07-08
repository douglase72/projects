package com.erdouglass.emdb.media.application.service;

import java.util.UUID;

import org.mapstruct.Named;

public interface CommonMapper {

  @Named("imageToString")
  default String imageToString(UUID image) {
    return image == null ? null : image + ".jpg";
  }
}
