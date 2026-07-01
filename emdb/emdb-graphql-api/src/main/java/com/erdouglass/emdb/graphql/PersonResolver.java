package com.erdouglass.emdb.graphql;

import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import com.erdouglass.emdb.media.person.PersonDto;
import com.erdouglass.emdb.media.person.PersonDto.PersonCredits;
import com.erdouglass.emdb.media.person.PersonQueryService;

@GraphQLApi
public class PersonResolver {

  @Inject
  PersonQueryService service;
  
  @Query("person") 
  public PersonDto findById(@Name("id") Long id) {
    return service.findById(id);
  }
  
  public @NotNull PersonCredits credits(@Source PersonDto person) {
    return service.findCreditsByPersonId(person.id());
  }
}
