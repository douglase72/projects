package com.erdouglass.emdb.media.internal;

import java.util.Map;

public interface PersonResolver {

  Map<Integer, PersonData> findOrCreate();
}
