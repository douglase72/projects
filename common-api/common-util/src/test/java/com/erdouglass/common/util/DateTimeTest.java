package com.erdouglass.common.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

class DateTimeTest {
  private static final Logger LOGGER = Logger.getLogger(DateTimeTest.class);
  
  @Test
  void testUtcOffset() {
    LocalDateTime ldt = LocalDateTime.now();
    DateTime dt = DateTimeFactory.now();
    
    LOGGER.infof("ldt: %s", ldt);
    LOGGER.infof("dt:  %s", dt);
  }
  
  @Test
  void testDateString() {
    DateTime dt = DateTimeFactory.from("2026-08-13 02:21:56.123456789");
    assertEquals("2026-08-13", dt.toDateString());
  }
  
  @Test
  void testParse() {
    DateTime dt0 = DateTimeFactory.from("2026-08-13");
    assertEquals("2026-08-13", dt0.toDateString());
    
    DateTime dt1 = DateTimeFactory.from("2026-08-13 02:21:56");
    assertEquals("2026-08-13 02:21:56.000", dt1.toMilliString());
    
    DateTime dt2 = DateTimeFactory.from("2026-08-13 02:21:56.123");
    assertEquals("2026-08-13 02:21:56.123", dt2.toMilliString());
    
    DateTime dt3 = DateTimeFactory.from("2026-08-13 02:21:56.123456");
    assertEquals("2026-08-13 02:21:56.123456", dt3.toMicroString());
    
    DateTime dt4 = DateTimeFactory.from("2026-08-13 02:21:56.123456789");
    assertEquals("2026-08-13 02:21:56.123456789", dt4.toNanoString());
  }
  
  @Test
  void testParseError() {
    var e1 = assertThrows(IllegalArgumentException.class, () -> DateTimeFactory.from("2026"));
    assertTrue(e1.getMessage().contains("2026"));
    
    var e2 = assertThrows(IllegalArgumentException.class, () -> DateTimeFactory.from("2026-13-01"));
    assertTrue(e2.getMessage().contains("2026-13-01"));
    
    var e3 = assertThrows(IllegalArgumentException.class, () -> DateTimeFactory.from("2026-02-30"));
    assertTrue(e3.getMessage().contains("2026-02-30"));
    
    var e4 = assertThrows(IllegalArgumentException.class, () -> DateTimeFactory.from(""));
    assertTrue(e4.getMessage().contains(""));
  }
}
