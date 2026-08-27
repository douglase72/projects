package com.erdouglass.emdb.media.movie.domain.command;

import java.math.BigDecimal;
import java.util.List;

import com.erdouglass.emdb.media.kernel.SourceId;

public interface SaveMovieCommand {

  SourceId sourceId();
  String title();
  String releaseDate();
  BigDecimal score();
  String originalLanguage();
  String overview();
  List<CastMember> cast();
  List<CrewMember> crew();
  
  record CastMember(String source, String id, String personId, String name, String character, Integer order) {}
  record CrewMember(String source, String id, String personId, String name, String job, String department) {}
}
