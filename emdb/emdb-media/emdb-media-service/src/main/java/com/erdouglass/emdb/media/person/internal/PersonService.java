package com.erdouglass.emdb.media.person.internal;

import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;

import com.erdouglass.emdb.media.internal.PersonData;
import com.erdouglass.emdb.media.internal.PersonResolver;

@ApplicationScoped
class PersonService implements PersonResolver {

  @Override
  public Map<Integer, PersonData> findOrCreate() {
    throw new UnsupportedOperationException();
  }
}
