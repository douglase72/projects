package com.erdouglass.emdb.media.person;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import com.erdouglass.emdb.media.query.PersonResponse;
import com.erdouglass.emdb.media.query.PersonResponse.Credits;

@GraphQLApi
public class GraphQLPersonResource {
  
  @Inject
  PersonService service;
  
  @Query("findPersonById") 
  public PersonResponse findById(@Name("id") Long id) {
    return service.findById(id);
  }
  
  public Credits credits(@Source PersonResponse person) {
    return service.findCreditsByPersonId(person.id());
  }
}
