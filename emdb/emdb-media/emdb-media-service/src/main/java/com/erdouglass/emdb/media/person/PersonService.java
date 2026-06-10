package com.erdouglass.emdb.media.person;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import com.erdouglass.common.graphql.ResourceNotFoundException;
import com.erdouglass.emdb.media.PersonCredit;
import com.erdouglass.emdb.media.command.SavePerson;
import com.erdouglass.emdb.media.credit.Credit;
import com.erdouglass.emdb.media.image.ImageService;
import com.erdouglass.emdb.media.internal.PersonResolver;
import com.erdouglass.emdb.media.logging.Log;
import com.erdouglass.emdb.media.movie.MovieCredit;
import com.erdouglass.emdb.media.query.PersonResponse;
import com.erdouglass.emdb.media.series.SeriesCredit;

/// Application service that persists [Person] aggregates and their profile
/// images, and resolves the people referenced by a batch of credits. Reconciles
/// each [SavePerson] command against existing records, inserting a new person or
/// merging an update, and cleaning up any image it replaces.
@ApplicationScoped
class PersonService implements PersonResolver {
  
  @Inject
  CreditRepository creditRepository;
  
  @Inject
  ImageService imageService;
  
  @Inject
  PersonMapper mapper;
  
  @Inject
  PersonRepository personRepository;
  
  /// Persists a person from the command, creating them when no person with the
  /// same TMDB id exists and updating the existing one otherwise. The profile
  /// image is saved or replaced as needed.
  ///
  /// @param command the person data to persist
  /// @return the saved person, with generated identifiers populated
  @Log
  @Transactional
  public PersonResponse save(final SavePerson command) {
    Person person;
    var existing = personRepository.findByTmdbId(command.tmdbId()).orElse(null); 
    if (existing == null) {
      var profile = imageService.save(command.profile());
      person = personRepository.insert(mapper.toPerson(command, profile));
    } else {
      var profile = imageService.update(existing.getTmdbProfile(), existing.getProfile(), command.profile());
      var cmd = SavePerson.builder(command)
          .profile(profile.image())
          .build();
      mapper.merge(cmd, existing);
      person = personRepository.update(existing);
      profile.toDelete().ifPresent(imageService::delete);
    }    
    return mapper.toPersonResponse(person);
  }
  
  /// Looks up a single person by id for read/query use.
  ///
  /// @param id the person id
  /// @return the person view
  /// @throws ResourceNotFoundException if no person has the given id  
  @Log
  @Transactional
  public PersonResponse findById(final Long id) {
    return personRepository.findById(id)
      .map(mapper::toPersonResponse)
      .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + id));    
  }
  
  /// Resolves all of a person's credits across both movies and series. Movie
  /// and series credits are fetched separately so each subtype's associations
  /// can be eagerly joined, then merged into a single list ordered by the
  /// referenced work's score, highest first.
  ///
  /// @param personId the person id
  /// @return the person's combined movie and series credits
  @Transactional
  public PersonResponse.Credits findCreditsByPersonId(final Long personId) {
    var credits = new ArrayList<Credit>();
    credits.addAll(creditRepository.findMovieCreditsByPersonId(personId));
    credits.addAll(creditRepository.findSeriesCreditsByPersonId(personId));
    credits.sort(Comparator.comparing(PersonService::score, Comparator.nullsLast(Comparator.reverseOrder())));
    return mapper.toCredits(credits);
  }

  @Override
  public Map<Integer, Person> findOrCreate(final List<PersonCredit> credits) {
    if (credits.isEmpty()) {
      return Map.of();
    }
    var distinct = credits.stream()
        .collect(Collectors.toMap(PersonCredit::tmdbId, Function.identity(), (a, _) -> a));
    var existing = personRepository.findByTmdbIdIn(List.copyOf(distinct.keySet())).stream()
        .collect(Collectors.toMap(Person::getTmdbId, Function.identity())); 
    var peopleToInsert = distinct.values().stream()
        .filter(c -> !existing.containsKey(c.tmdbId()))
        .map(PersonService::toPerson)
        .toList();
    for (var person : personRepository.insertAll(peopleToInsert)) {
      var tmdbId = person.getTmdbId();
      existing.put(tmdbId, person);
    }
    return existing;    
  }
  
  /// Extracts the score of the work a credit belongs to — the movie's score for
  /// a [MovieCredit], the series' score for a [SeriesCredit] — for use as the
  /// cross-type sort key. Both associations are eagerly fetched by the queries
  /// feeding this, so reading them does not trigger additional loads.
  ///
  /// @param credit the credit to read the work score from
  /// @return the work's score, or {@code null} if the type is unrecognized
  private static Float score(final Credit credit) {
    return switch (credit) {
      case MovieCredit m  -> m.getMovie().getScore();
      case SeriesCredit s -> s.getSeries().getScore();
      default -> null;
    };
  }
  
  /// Builds a new transient [Person] from a credit, copying the TMDB id, name,
  /// and gender. Used to create people that do not yet exist during batch
  /// resolution.
  ///
  /// @param credit the credit to derive a person from
  /// @return a new, unpersisted person
  private static Person toPerson(final PersonCredit credit) {
    var person = new Person(credit.tmdbId());
    person.setName(credit.name());
    person.setGender(credit.gender());
    return person;
  }  
}
