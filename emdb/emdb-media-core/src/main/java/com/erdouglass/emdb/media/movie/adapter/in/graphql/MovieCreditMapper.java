package com.erdouglass.emdb.media.movie.adapter.in.graphql;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

import com.erdouglass.emdb.media.movie.application.port.in.CreditType;
import com.erdouglass.emdb.media.movie.application.port.in.MovieCreditView;

@Mapper(
    componentModel = "cdi",
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
interface MovieCreditMapper {

  default MovieCreditResponse toMovieCreditResponse(List<MovieCreditView> views) {
    if (views == null || views.isEmpty()) {
      return new MovieCreditResponse(List.of(), List.of());
    }
    var byType = views.stream().collect(Collectors.groupingBy(MovieCreditView::creditType));
    return new MovieCreditResponse(
        toCastCredits(byType.getOrDefault(CreditType.CAST, List.of())),
        toCrewCredits(byType.getOrDefault(CreditType.CREW, List.of())));
  }

  @Mapping(target = "character", source = "role")
  MovieCreditResponse.CastCredit toCastCredit(MovieCreditView view);

  List<MovieCreditResponse.CastCredit> toCastCredits(List<MovieCreditView> views);

  @Mapping(target = "job", source = "role")
  MovieCreditResponse.CrewCredit toCrewCredit(MovieCreditView view);

  List<MovieCreditResponse.CrewCredit> toCrewCredits(List<MovieCreditView> views);
}
