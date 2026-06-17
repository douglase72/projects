package com.erdouglass.emdb.media.person;

import jakarta.inject.Inject;
import jakarta.validation.Valid;

import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import com.erdouglass.common.graphql.ResourceNotFoundException;
import com.erdouglass.emdb.media.query.OffsetPage;
import com.erdouglass.emdb.media.query.PersonResponse;
import com.erdouglass.emdb.media.query.PersonResponse.Credits;

/// GraphQL API for querying [Person] data. Exposes a person lookup by id and a
/// field resolver that lazily attaches the person's credits, so credits are
/// fetched only when a query selects them rather than on every person read.
@GraphQLApi
public class GraphQLPersonResource {
  
  @Inject
  PersonService service;
  
  @Query("allPeople") 
  public OffsetPage<PersonResponse> findAll(@Valid @Name("query") PersonQuery query) {
    return service.findAll(query);
  }
  
  /// Looks up a single person by their primary key.
  ///
  /// @param id the person id
  /// @return the person
  /// @throws ResourceNotFoundException if no person has the given id
  @Query("person") 
  public PersonResponse findById(@Name("id") Long id) {
    return service.findById(id);
  }
  
  /// Field resolver that supplies the `credits` field of a [PersonResponse],
  /// invoked by the GraphQL engine only when a query selects credits. The
  /// credits are not a field of [PersonResponse] itself; they are resolved
  /// separately here against the parent person's id.
  ///
  /// @param person the parent person the credits belong to
  /// @return the person's cast & crew credits across movies and series
  public Credits credits(@Source PersonResponse person) {
    return service.findCreditsByPersonId(person.id());
  }
}
