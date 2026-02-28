package com.erdouglass.emdb.common.query;

import java.util.UUID;

import com.erdouglass.emdb.common.MediaType;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME, 
    include = JsonTypeInfo.As.EXISTING_PROPERTY, 
    property = "type",
    visible = true
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = PersonMovieCreditDto.class, name = "movie"),
    @JsonSubTypes.Type(value = PersonSeriesCreditDto.class, name = "series")
})
public interface PersonCreditDto {
  
  String backdrop();
  UUID creditId();
  Long id();
  String overview();
  String poster();
  Float score();
  String title();
  MediaType type();

}
