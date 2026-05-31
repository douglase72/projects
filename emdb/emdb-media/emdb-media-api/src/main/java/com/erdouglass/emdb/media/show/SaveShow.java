package com.erdouglass.emdb.media.show;

import com.erdouglass.emdb.media.Image;
import com.erdouglass.emdb.media.SaveCommand;
import com.erdouglass.emdb.media.movie.SaveMovie;
import com.erdouglass.emdb.media.series.SaveSeries;

public sealed interface SaveShow extends SaveCommand permits SaveMovie, SaveSeries {

  String title();
  
  Float score();
  
  ShowStatus status();
  
  Image backdrop();
  
  Image poster();
  
  String originalLanguage();
  
  String tagline();
  
  String overview();
}
