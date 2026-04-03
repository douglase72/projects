package com.erdouglass.emdb.media.mapper;

import java.util.List;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.common.comand.SaveSeries;
import com.erdouglass.emdb.common.comand.UpdateSeries;
import com.erdouglass.emdb.media.dto.SaveResult;
import com.erdouglass.emdb.media.entity.Role;
import com.erdouglass.emdb.media.entity.Series;
import com.erdouglass.emdb.media.entity.SeriesCredit;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesRequest;
import com.erdouglass.emdb.media.proto.v1.SaveSeriesResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesCastCreditResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesCreditResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesCrewCreditResponse;
import com.erdouglass.emdb.media.proto.v1.SeriesResponse;
import com.erdouglass.emdb.media.proto.v1.UpdateSeriesCommand;

@Mapper(
    componentModel = "cdi", 
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.IGNORE,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface SeriesMapper {
  
  @Mapping(target = "credits", ignore = true)
  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "backdrop.tmdbName", target = "tmdbBackdrop")
  @Mapping(source = "poster.name", target = "poster")
  @Mapping(source = "poster.tmdbName", target = "tmdbPoster")
  void merge(SaveSeries command, @MappingTarget Series series);  
  void merge(UpdateSeries command, @MappingTarget Series series);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  SaveSeries toSaveSeries(SaveSeriesRequest request);
  
  @Mapping(target = "credits", ignore = true)
  @Mapping(target = "people", ignore = true)
  @Mapping(target = "backdrop", expression = "java(series.getBackdrop() != null ? com.erdouglass.emdb.common.comand.Image.of(series.getBackdrop(), series.getTmdbBackdrop()) : null)")
  @Mapping(target = "poster", expression = "java(series.getPoster() != null ? com.erdouglass.emdb.common.comand.Image.of(series.getPoster(), series.getTmdbPoster()) : null)")
  SaveSeries toSaveSeries(Series series);
  
  @BeanMapping(builder = @Builder(disableBuilder = true))
  UpdateSeries toUpdateSeries(UpdateSeriesCommand command);
  
  @Mapping(target = "credits", ignore = true)
  @Mapping(source = "backdrop.name", target = "backdrop")
  @Mapping(source = "backdrop.tmdbName", target = "tmdbBackdrop")
  @Mapping(source = "poster.name", target = "poster")
  @Mapping(source = "poster.tmdbName", target = "tmdbPoster")
  Series toSeries(SaveSeries command);
  
  @Mapping(source = "role", target = "character")
  com.erdouglass.emdb.media.proto.v1.Role toRoleResponse(Role role);
  
  @Mapping(source = "role", target = "title")
  com.erdouglass.emdb.media.proto.v1.Job toJobResponse(Role role);
    
  @Mapping(source = "entity", target = "series")
  SaveSeriesResponse toSaveSeriesResponse(SaveResult<Series> result);
  
  @Mapping(source = "id", target = "creditId")
  @Mapping(source = "person.id", target = "id")
  @Mapping(source = "person.name", target = "name")
  @Mapping(source = "person.gender", target = "gender")
  @Mapping(source = "person.profile", target = "profile")
  SeriesCastCreditResponse toSeriesCastCreditResponse(SeriesCredit credit);

  @Mapping(source = "id", target = "creditId")
  @Mapping(source = "person.id", target = "id")
  @Mapping(source = "person.name", target = "name")
  @Mapping(source = "person.gender", target = "gender")
  @Mapping(source = "person.profile", target = "profile")
  @Mapping(source = "roles", target = "jobs")
  SeriesCrewCreditResponse toSeriesCrewCreditResponse(SeriesCredit credit);  
  
  default SeriesCreditResponse toSeriesCreditResponse(List<SeriesCredit> credits) {
    if (credits == null || credits.isEmpty()) return SeriesCreditResponse.getDefaultInstance();
    var builder = SeriesCreditResponse.newBuilder();
    for (var credit : credits) {
        switch (credit.getType()) {
            case CAST -> builder.addCast(toSeriesCastCreditResponse(credit));
            case CREW -> builder.addCrew(toSeriesCrewCreditResponse(credit));
        }
    }
    return builder.build();
  }
  
  SeriesResponse toSeriesResponse(Series series);

}
