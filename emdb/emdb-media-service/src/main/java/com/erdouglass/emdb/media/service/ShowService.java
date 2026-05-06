package com.erdouglass.emdb.media.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import com.erdouglass.emdb.media.api.Image;
import com.erdouglass.emdb.media.entity.Show;
import com.erdouglass.emdb.media.query.TmdbShow;
import com.google.common.base.Objects;

@ApplicationScoped
public class ShowService {

  @Inject
  TmdbImageService imageService;
  
  public void deleteImages(Show oldShow, Show newShow) {
    if (!Objects.equal(oldShow.getTmdbBackdrop(), newShow.getTmdbBackdrop())) {
      if (oldShow.getBackdrop() != null) {
        imageService.delete(oldShow.getBackdrop());
      }
    }
    
    if (!Objects.equal(oldShow.getTmdbPoster(), newShow.getTmdbPoster())) {
      if (oldShow.getPoster() != null) {
        imageService.delete(oldShow.getPoster());
      }
    }    
  }
  
  public Image extractBackdrop(Show show, TmdbShow tmdbShow) {
    var image = Image.of(show.getBackdrop(), show.getTmdbBackdrop());
    if (show.getBackdrop() == null || !Objects.equal(show.getTmdbBackdrop(), tmdbShow.backdrop_path())) {
      image = Image.of(imageService.save(tmdbShow.backdrop_path()), tmdbShow.backdrop_path());
    }
    return image;
  }
  
  public Image extractPoster(Show show, TmdbShow tmdbShow) {
    var image = Image.of(show.getPoster(), show.getTmdbPoster());
    if (show.getPoster() == null || !Objects.equal(show.getTmdbPoster(), tmdbShow.poster_path())) {
      image =  Image.of(imageService.save(tmdbShow.poster_path()), tmdbShow.poster_path());
    }
    return image;
  } 
}
