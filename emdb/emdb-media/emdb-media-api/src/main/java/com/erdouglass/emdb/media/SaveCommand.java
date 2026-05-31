package com.erdouglass.emdb.media;

import com.erdouglass.emdb.media.person.SavePerson;
import com.erdouglass.emdb.media.show.SaveShow;

public sealed interface SaveCommand permits SaveShow, SavePerson {

  Integer tmdbId();
  
  String homepage();
}
