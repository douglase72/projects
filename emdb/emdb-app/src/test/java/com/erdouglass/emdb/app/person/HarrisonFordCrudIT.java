package com.erdouglass.emdb.app.person;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.TestMethodOrder;

@TestInstance(Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class HarrisonFordCrudIT {
  private static final Logger LOGGER = Logger.getLogger(HarrisonFordCrudIT.class);
  
}
