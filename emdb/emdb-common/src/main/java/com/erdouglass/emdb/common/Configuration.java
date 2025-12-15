package com.erdouglass.emdb.common;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Configuration {
  public static final String AUDIT_KEY = "audit.log";
  public static final int ISO_639_1_LENGTH = 2;
  public static final int URL_MAX_LENGTH = 2048;
}
