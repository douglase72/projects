package com.erdouglass.emdb.media.domain.internal;

import com.erdouglass.emdb.media.person.Gender;

public interface PersonCredit {

  Integer tmdbId();
  
  String name();
  
  Gender gender();
  
  String profile();  
}
