package com.erdouglass.emdb.media;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public interface MediaFacade {
  
  SaveResult saveMovie(@NotNull @Valid SaveMovie command);
  
  SaveResult savePerson(@NotNull @Valid SavePerson command);
  
  SaveResult saveSeries(@NotNull @Valid SaveSeries command);
}
