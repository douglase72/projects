package com.erdouglass.emdb.ingest.application.port.out;

public interface Media {
  
  void save(Movie movie);
  
  void save(Person person);
}
