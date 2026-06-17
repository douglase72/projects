package com.erdouglass.emdb.media.movie;

import jakarta.data.Sort;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.graphql.DefaultValue;

public record MovieQuery(
    @Positive @DefaultValue("1") Long page,
    @Positive @DefaultValue("20") Integer size,
    @DefaultValue("ID_DESC") MovieSort sort) {
  
  public enum MovieSort {
    RELEASE_DATE_ASC(Sort.asc(_Movie.RELEASE_DATE)),
    RELEASE_DATE_DESC(Sort.desc(_Movie.RELEASE_DATE)),
    ID_ASC(Sort.asc(_Movie.ID)),
    ID_DESC(Sort.desc(_Movie.ID)),
    SCORE_ASC(Sort.asc(_Movie.SCORE)),
    SCORE_DESC(Sort.desc(_Movie.SCORE));    
  
    private final Sort<Movie> sortOrder;
  
    MovieSort(final Sort<Movie> sortOrder) {
      this.sortOrder = sortOrder;
    }
    
    public Sort<Movie> sortOrder() {
      return sortOrder;
    }    
  }
}
