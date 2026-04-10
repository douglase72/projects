package com.erdouglass.emdb.media.mapper;

import java.util.UUID;

import org.mapstruct.Named;

import com.erdouglass.emdb.common.Image;

public interface ShowMapper {
  
  @Named("imageToUuid")
  default UUID imageToUuid(Image image) {
    return image != null ? image.name() : null;
  }

  @Named("imageToTmdbName")
  default String imageToTmdbName(Image image) {
    return image != null ? image.tmdbName() : null;
  }  
}
