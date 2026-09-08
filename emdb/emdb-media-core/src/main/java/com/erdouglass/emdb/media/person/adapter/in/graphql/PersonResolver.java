package com.erdouglass.emdb.media.person.adapter.in.graphql;

import java.util.UUID;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;

import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.person.application.port.in.FindPersonUseCase;

@GraphQLApi
public class PersonResolver {
  
  @Inject
  FindPersonUseCase findUseCase;
  
  @Inject
  PersonMapper mapper;

  @Query("person")
  @Description("A single person by their catalogue id.")
  public PersonResponse person(@Name("id") @NonNull UUID id) {
    return findUseCase.findById(PublicId.of(id))
        .map(mapper::toPersonResponse)
        .orElse(null);    
  }
}
