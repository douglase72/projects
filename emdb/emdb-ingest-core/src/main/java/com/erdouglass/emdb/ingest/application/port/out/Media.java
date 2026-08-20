package com.erdouglass.emdb.ingest.application.port.out;

import com.erdouglass.emdb.ingest.application.dto.Movie;
import com.erdouglass.emdb.ingest.application.dto.Person;

public interface Media {

  void save(Movie movie);
  
  void save(Person person);
}
