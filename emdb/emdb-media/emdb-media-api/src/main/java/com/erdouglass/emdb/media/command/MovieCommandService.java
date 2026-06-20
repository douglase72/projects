package com.erdouglass.emdb.media.command;

import com.erdouglass.emdb.media.query.MovieDto;

public interface MovieCommandService {

  MovieDto save(SaveMovie command);
  
  MovieDto update(UpdateMovie command);
  
  void delete(Long id);
}
