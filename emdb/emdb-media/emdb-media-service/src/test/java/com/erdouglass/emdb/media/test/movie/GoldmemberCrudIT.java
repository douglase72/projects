package com.erdouglass.emdb.media.test.movie;

import java.io.IOException;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class GoldmemberCrudIT {
  private static final Logger LOGGER = Logger.getLogger(GoldmemberCrudIT.class);
  
  @Test
  @Order(1)
  void testSaveMovie() throws IOException, InterruptedException {

  }
}
