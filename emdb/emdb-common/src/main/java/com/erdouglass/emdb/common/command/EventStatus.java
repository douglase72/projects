package com.erdouglass.emdb.common.command;

public enum EventStatus {
  QUEUED,
  STARTED,
  FETCHED,
  PROCESSED,
  PERSISTED,
  COMPLETED,
  FAILED;
}
