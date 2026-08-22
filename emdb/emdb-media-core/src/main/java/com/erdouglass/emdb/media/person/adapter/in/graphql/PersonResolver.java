package com.erdouglass.emdb.media.person.adapter.in.graphql;

import jakarta.inject.Inject;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;
import org.jboss.logging.Logger;

import com.erdouglass.emdb.media.person.application.port.in.FindPersonUseCase;
import com.erdouglass.emdb.media.person.domain.PersonPublicId;

/// GraphQL entry point for reading people.
///
/// Reads only. Writes go through the REST resource, so the two protocols split
/// along the CQRS seam rather than duplicating one another: GraphQL serves
/// clients that want to shape their reads, REST serves ingestion and editing.
///
/// The resolver holds no logic beyond translating an incoming id into a domain
/// value object and an absent result into a GraphQL `null`.
@GraphQLApi
public class PersonResolver {
  private static final Logger LOGGER = Logger.getLogger(PersonResolver.class);
  
  @Inject
  FindPersonUseCase findUseCase;
  
  @Inject
  PersonMapper mapper;
  
  /// Looks up a single person by its catalogue id.
  ///
  /// A missing person is not an error here: the method returns `null`, which
  /// GraphQL surfaces as a null field rather than as an error entry. A
  /// malformed id *is* an error, raised while parsing the id rather than after a
  /// futile lookup.
  ///
  /// @param id the catalogue id in prefixed form, e.g. `pr_3`
  /// @return the projected person, or `null` if no person carries that id
  /// @throws IllegalArgumentException if `id` is not a well-formed catalogue id
  @Query("person")
  @Description("A single person by its catalogue id.")
  public PersonResponse person(@Name("id") @NonNull String id) {
    var person = findUseCase.findById(PersonPublicId.of(id))
        .map(mapper::toPersonResponse);
    person.ifPresent(m -> LOGGER.debugf("Found: %s", m));
    return person.orElse(null);
  }
}