package com.erdouglass.emdb.media.core.person;

import java.util.List;
import java.util.Map;

import com.erdouglass.emdb.media.person.PersonCredit;

public interface PersonResolver {
  
  Map<Integer, Person> findOrCreate(List<PersonCredit> credits);
}
