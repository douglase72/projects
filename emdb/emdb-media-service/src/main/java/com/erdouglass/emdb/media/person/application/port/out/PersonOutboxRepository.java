package com.erdouglass.emdb.media.person.application.port.out;

import java.util.List;

import com.erdouglass.emdb.media.person.domain.event.DomainEvent;

public interface PersonOutboxRepository {

  void saveAll(List<DomainEvent> events);
}
