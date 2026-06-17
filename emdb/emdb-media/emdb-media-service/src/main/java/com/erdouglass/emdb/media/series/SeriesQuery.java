package com.erdouglass.emdb.media.series;

import jakarta.data.Sort;
import jakarta.validation.constraints.Positive;

import org.eclipse.microprofile.graphql.DefaultValue;

public record SeriesQuery(
    @Positive @DefaultValue("1") Long page,
    @Positive @DefaultValue("20") Integer size,
    @DefaultValue("ID_DESC") SeriesSort sort) {

  public enum SeriesSort {
    FIRST_AIR_DATE_ASC(Sort.asc(_Series.FIRST_AIR_DATE)),
    FIRST_AIR_DATE_DESC(Sort.desc(_Series.FIRST_AIR_DATE)),
    ID_ASC(Sort.asc(_Series.ID)),
    ID_DESC(Sort.desc(_Series.ID)),
    SCORE_ASC(Sort.asc(_Series.SCORE)),
    SCORE_DESC(Sort.desc(_Series.SCORE));    
  
    private final Sort<Series> sortOrder;
  
    SeriesSort(final Sort<Series> sortOrder) {
      this.sortOrder = sortOrder;
    }
    
    public Sort<Series> sortOrder() {
      return sortOrder;
    }    
  }
}
