package com.erdouglass.emdb.common.query;

import java.util.UUID;

import com.erdouglass.emdb.common.MediaType;
import com.erdouglass.emdb.common.query.PersonDto.MovieCredit;
import com.erdouglass.emdb.common.query.PersonDto.SeriesCredit;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = MovieCredit.class, name = "MOVIE"),
    @JsonSubTypes.Type(value = SeriesCredit.class, name = "SERIES")
})
public sealed interface PersonCredit permits MovieCredit, SeriesCredit {
  
  String backdrop();
  UUID creditId();
  Long id();
  String overview();
  String poster();
  Float score();
  String title();
  MediaType type();

}
