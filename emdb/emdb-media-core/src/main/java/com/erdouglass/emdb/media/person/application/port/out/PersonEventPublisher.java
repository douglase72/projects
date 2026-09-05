package com.erdouglass.emdb.media.person.application.port.out;

import java.util.List;

import com.erdouglass.emdb.media.api.PersonStubCreated;

public interface PersonEventPublisher {

  void publish(List<PersonStubCreated> events);
}
