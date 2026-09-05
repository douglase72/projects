package com.erdouglass.emdb.media.movie.adapter.in.rest;

import java.util.stream.Stream;

import com.erdouglass.emdb.media.api.TmdbId;
import com.erdouglass.emdb.media.kernel.CastOrder;
import com.erdouglass.emdb.media.kernel.Department;
import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Name;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.Role;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.TmdbCreditId;
import com.erdouglass.emdb.media.movie.adapter.in.rest.SaveMovieRequest.CastMember;
import com.erdouglass.emdb.media.movie.adapter.in.rest.SaveMovieRequest.CrewMember;
import com.erdouglass.emdb.media.movie.application.port.in.CreditSpec;
import com.erdouglass.emdb.media.movie.application.port.in.CreditSpec.CastSpec;
import com.erdouglass.emdb.media.movie.application.port.in.CreditSpec.CrewSpec;
import com.erdouglass.emdb.media.movie.application.port.in.SaveMovieCommand;
import com.erdouglass.emdb.media.movie.domain.model.MovieDetails;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;

final class CommandMapper {

  private CommandMapper() { }
  
  public static SaveMovieCommand toSaveMovieCommand(Integer tmdbId, SaveMovieRequest request) {
    var details = MovieDetails.builder()
        .title(Title.of(request.title()))
        .releaseDate(request.releaseDate() != null ? ReleaseDate.from(request.releaseDate()) : null)
        .score(request.score() != null ? Score.of(request.score()) : null)
        .originalLanguage(request.originalLanguage() != null ? LanguageCode.of(request.originalLanguage()) : null)
        .overview(request.overview() != null ? Overview.of(request.overview()) : null)
        .build();
    var credits = Stream.<CreditSpec>concat(
        request.cast() != null ? request.cast().stream().map(CommandMapper::toCastSpec) : Stream.empty(),
        request.crew() != null ? request.crew().stream().map(CommandMapper::toCrewSpec) : Stream.empty())
        .toList();
    return SaveMovieCommand.of(TmdbId.of(tmdbId), details, credits);
  }
  
  private static CastSpec toCastSpec(CastMember member) {
    return CastSpec.builder()
        .tmdbId(TmdbCreditId.of(member.tmdbCreditId()))
        .personId(TmdbId.of(member.tmdbPersonId()))
        .name(Name.of(member.name()))
        .character(Role.of(member.character()))
        .order(CastOrder.of(member.order()))
        .build();
  }
  
  private static CrewSpec toCrewSpec(CrewMember member) {
    return CrewSpec.builder()
        .tmdbId(TmdbCreditId.of(member.tmdbCreditId()))
        .personId(TmdbId.of(member.tmdbPersonId()))
        .name(Name.of(member.name()))
        .job(Role.of(member.job()))
        .department(Department.of(member.department()))
        .build();
  }
}
