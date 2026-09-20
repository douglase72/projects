package com.erdouglass.emdb.media.movie.application.service;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

import com.erdouglass.emdb.media.SaveMovieCommand;
import com.erdouglass.emdb.media.SaveMovieCommand.CastMember;
import com.erdouglass.emdb.media.SaveMovieCommand.CrewMember;
import com.erdouglass.emdb.media.kernel.CastOrder;
import com.erdouglass.emdb.media.kernel.Department;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Role;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.kernel.TmdbId;
import com.erdouglass.emdb.media.movie.application.port.in.UpdateMovieCommand;
import com.erdouglass.emdb.media.movie.domain.model.CastCredit;
import com.erdouglass.emdb.media.movie.domain.model.CastDetails;
import com.erdouglass.emdb.media.movie.domain.model.CrewCredit;
import com.erdouglass.emdb.media.movie.domain.model.CrewDetails;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.model.MovieDto;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;
import com.erdouglass.emdb.media.person.domain.model.Name;

final class MovieMapper {

  private MovieMapper() { }
  
  public static MovieDto toMovieDto(SaveMovieCommand command, Map<TmdbId, PublicId> people) {
    var details = MovieDetails.builder()
        .title(Title.of(command.title()))
        .releaseDate(command.releaseDate() != null ? ReleaseDate.from(command.releaseDate()) : null)
        .score(command.score() != null ? Score.of(command.score()) : null)
        .originalLanguage(command.originalLanguage() != null ? LanguageCode.of(command.originalLanguage()) : null)
        .overview(command.overview() != null ? Overview.of(command.overview()) : null)
        .build();
    var cast = Stream.ofNullable(command.cast())
        .flatMap(Collection::stream)
        .map(c -> toCastCredit(c, people.get(TmdbId.of(c.tmdbPersonId()))));
    var crew = Stream.ofNullable(command.crew())
        .flatMap(Collection::stream)
        .map(c -> toCrewCredit(c, people.get(TmdbId.of(c.tmdbPersonId()))));
    return MovieDto.of(details, Stream.concat(cast, crew).toList());
  }
  
  public static MovieDetails toMovieDetails(UpdateMovieCommand command) {
    return MovieDetails.builder()
        .title(Title.of(command.title()))
        .releaseDate(command.releaseDate() != null ? ReleaseDate.from(command.releaseDate()) : null)
        .score(command.score() != null ? Score.of(command.score()) : null)
        .originalLanguage(command.originalLanguage() != null ? LanguageCode.of(command.originalLanguage()) : null)
        .overview(command.overview() != null ? Overview.of(command.overview()) : null)
        .build();
  }
  
  private static CastCredit toCastCredit(CastMember member, PublicId personId) {
    var details = CastDetails.builder()
        .tmdbId(TmdbCreditId.of(member.tmdbCreditId()))
        .personId(personId)
        .name(Name.of(member.name()))
        .character(member.character() != null ? Role.of(member.character()) : null)
        .order(CastOrder.of(member.order()))
        .build();
    return CastCredit.create(details);
  }
  
  private static CrewCredit toCrewCredit(CrewMember member, PublicId personId) {
    var details = CrewDetails.builder()
        .tmdbId(TmdbCreditId.of(member.tmdbCreditId()))
        .personId(personId)
        .name(Name.of(member.name()))
        .job(member.job() != null ? Role.of(member.job()) : null)
        .department(member.department() != null ? Department.of(member.department()) : null)
        .build();
    return CrewCredit.create(details);
  }
}
