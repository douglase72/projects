package com.erdouglass.emdb.media.mapper;

import java.util.UUID;

import org.mapstruct.Named;

import com.erdouglass.emdb.common.Image;
import com.erdouglass.emdb.media.entity.Show;

public interface CommonMapper {
  
  @Named("imageToUuid")
  default UUID imageToUuid(Image image) {
    return image != null ? image.name() : null;
  }

  @Named("imageToTmdbName")
  default String imageToTmdbName(Image image) {
    return image != null ? image.tmdbName() : null;
  } 
  
  @Named("backdropToImage")
  default Image backdropToImage(Show show) {
    if (show.getBackdrop() == null && show.getTmdbBackdrop() == null) {
      return null;
    }
    return Image.of(show.getBackdrop(), show.getTmdbBackdrop());
  }
  
  @Named("posterToImage")
  default Image posterToImage(Show show) {
    if (show.getPoster() == null && show.getTmdbPoster() == null) {
      return null;
    }
    return Image.of(show.getPoster(), show.getTmdbPoster());
  } 
}
