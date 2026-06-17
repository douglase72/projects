package com.erdouglass.emdb.media.person;

import jakarta.data.Sort;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.graphql.DefaultValue;

public record PersonQuery(
    @Positive @DefaultValue("1") Long page,
    @Positive @DefaultValue("20") Integer size,
    @DefaultValue("ID_DESC") PersonSort sort) {

  public enum PersonSort {
    BIRTH_DATE_ASC(Sort.asc(_Person.BIRTH_DATE)),
    BIRTH_DATE_DESC(Sort.desc(_Person.BIRTH_DATE)),
    ID_ASC(Sort.asc(_Person.ID)),
    ID_DESC(Sort.desc(_Person.ID));    
  
    private final Sort<Person> sortOrder;
  
    PersonSort(final Sort<Person> sortOrder) {
      this.sortOrder = sortOrder;
    }
    
    public Sort<Person> sortOrder() {
      return sortOrder;
    }    
  }
}
