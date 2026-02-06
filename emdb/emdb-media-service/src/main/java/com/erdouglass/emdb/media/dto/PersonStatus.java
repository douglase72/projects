package com.erdouglass.emdb.media.dto;

import com.erdouglass.emdb.media.entity.Person;

public record PersonStatus(Person person, Status status) {
  
  public enum Status {
    CREATED,
    DELETED,
    UPDATED,
    UNCHANGED;
  }  
  
  public static PersonStatus of(Person person, Status status) {
    return new PersonStatus(person, status);
  }

}