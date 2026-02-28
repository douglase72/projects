package com.erdouglass.emdb.common;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Configuration {
  public static final int ISO_639_1_LENGTH = 2;
  public static final int URL_MAX_LENGTH = 2048;
  public static final String APPEND = "append";
  public static final String JOB_ID = "job-id";
  public static final String JOB_START_TIME = "job-start-time";
  public static final String MEDIA_KEY = "ingest.media";
  public static final String ORIGINAL = "original";
}
