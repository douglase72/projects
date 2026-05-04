package com.erdouglass.emdb.media.mapper;

import java.util.List;

import jakarta.data.page.Page;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.api.Gender;
import com.erdouglass.emdb.media.api.Image;
import com.erdouglass.emdb.media.api.command.SavePerson;
import com.erdouglass.emdb.media.api.command.UpdatePerson;
import com.erdouglass.emdb.media.api.query.PersonQueryParameters;
import com.erdouglass.emdb.media.api.query.PersonView;
import com.erdouglass.emdb.media.entity.Person;
import com.erdouglass.emdb.media.proto.v1.FindAllPersonRequest;
import com.erdouglass.emdb.media.proto.v1.PersonPageResponse;
import com.erdouglass.emdb.media.proto.v1.PersonResponse;
import com.erdouglass.emdb.media.proto.v1.PersonViewResponse;
import com.erdouglass.emdb.media.proto.v1.SavePeopleResponse;
import com.erdouglass.emdb.media.proto.v1.SavePersonRequest;
import com.erdouglass.emdb.media.proto.v1.SavePersonResponse;
import com.erdouglass.emdb.media.proto.v1.UpdatePersonCommand;
import com.erdouglass.emdb.media.query.SaveResult;
import com.erdouglass.emdb.media.query.TmdbPerson;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface PersonMapper extends CommonMapper {
  
  @Named("genderFromId")
  default Gender genderFromId(Integer id) {
    return Gender.from(id);
  }
  
  @Named("profileToImage")
  default Image profileToImage(Person person) {
    if (person.getProfile() == null && person.getTmdbProfile() == null) {
      return null;
    }
    return Image.of(person.getProfile(), person.getTmdbProfile());
  }  
    
  @Mapping(source = "profile", target = "profile", qualifiedByName = "imageToUuid")
  @Mapping(source = "profile", target = "tmdbProfile", qualifiedByName = "imageToTmdbName")
  void merge(SavePerson command, @MappingTarget Person person);
  
  @Mapping(target = "tmdbId", ignore = true)
  @Mapping(target = "tmdbProfile", ignore = true)
  void merge(UpdatePerson command, @MappingTarget Person person);

  @Mapping(target = "homepage", ignore = true)
  @BeanMapping(builder = @Builder(disableBuilder = true))
  SavePerson toSavePerson(SavePersonRequest request);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "person", target = "profile", qualifiedByName = "profileToImage")
  SavePerson toSavePerson(Person person);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  @Mapping(source = "person.id", target = "tmdbId")
  @Mapping(source = "person.name", target = "name")
  @Mapping(source = "person.birthday", target = "birthDate")
  @Mapping(source = "person.deathday", target = "deathDate")
  @Mapping(source = "person.place_of_birth", target = "birthPlace")
  @Mapping(source = "person.gender", target = "gender", qualifiedByName = "genderFromId")
  SavePerson toSavePerson(TmdbPerson person, Image profile);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  List<SavePerson> toSavePeople(List<SavePersonRequest> requests);
    
  @InheritConfiguration(name = "merge")
  Person toPerson(SavePerson command);
  
  PersonQueryParameters toPersonQueryParameters(FindAllPersonRequest request);
  
  @Mapping(target = "homepage", ignore = true)
  @BeanMapping(builder = @Builder(disableBuilder = true))
  UpdatePerson toUpdatePerson(UpdatePersonCommand command);
      
  @Mapping(source = "entity", target = "person")
  SavePersonResponse toSavePersonResponse(SaveResult<Person> result);
  
  @Mapping(source = "entity.id", target = "id")
  @Mapping(source = "entity.tmdbId", target = "tmdbId")
  com.erdouglass.emdb.media.proto.v1.SaveResult toSaveResult(SaveResult<Person> result);

  default SavePeopleResponse toSavePeopleResponse(List<SaveResult<Person>> results) {
    if (results == null) return null;
    var builder = SavePeopleResponse.newBuilder();
    results.stream()
        .map(this::toSaveResult)
        .forEach(builder::addResults);
    return builder.build();
  }  
    
  PersonResponse toPersonResponse(Person person);
  
  PersonViewResponse toPersonViewResponse(PersonView view);
  
  default PersonPageResponse toPersonPageResponse(Page<PersonView> page) {
    var builder = PersonPageResponse.newBuilder()
        .setPage((int) page.pageRequest().page())
        .setSize(page.pageRequest().size())
        .setHasNext(page.hasNext());
    page.content().stream()
        .map(this::toPersonViewResponse)
        .forEach(builder::addResults);
    return builder.build();
  }
}
