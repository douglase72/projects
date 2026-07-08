package com.erdouglass.emdb.media.adapter.in.graphql;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;

import com.erdouglass.emdb.media.application.port.in.PersonQueryService;
import com.erdouglass.emdb.media.application.port.in.PersonView;

@GraphQLApi
public class PersonResolver {

  @Inject
  PersonQueryService service;
  
  @Query("person") 
  public PersonView findById(@Name("id") Long id) {
    return service.findById(id);
  }
}
