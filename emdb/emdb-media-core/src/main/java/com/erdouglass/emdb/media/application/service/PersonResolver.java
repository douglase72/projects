package com.erdouglass.emdb.media.application.service;

import java.util.List;
import java.util.Map;

import com.erdouglass.emdb.media.PersonCredit;

public interface PersonResolver {
  
  Map<Long, Long> findOrCreate(List<PersonCredit> credits);
}
