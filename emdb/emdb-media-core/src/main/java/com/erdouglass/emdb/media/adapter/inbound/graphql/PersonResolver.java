package com.erdouglass.emdb.media.adapter.inbound.graphql;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import com.erdouglass.emdb.media.application.port.inbound.person.PersonQueryService;
import com.erdouglass.emdb.media.application.port.inbound.person.PersonView;

@GraphQLApi
public class PersonResolver {

  @Inject
  PersonQueryService service;
  
  @Query("person") 
  public PersonView findById(@Name("id") Long id) {
    return service.findById(id);
  }
}
