package com.erdouglass.emdb.media.movie.domain.command;

import com.erdouglass.emdb.media.kernel.LanguageCode;
import com.erdouglass.emdb.media.kernel.Overview;
import com.erdouglass.emdb.media.kernel.PublicId;
import com.erdouglass.emdb.media.kernel.Score;
import com.erdouglass.emdb.media.kernel.Title;
import com.erdouglass.emdb.media.kernel.Version;
import com.erdouglass.emdb.media.movie.domain.model.ReleaseDate;

public interface UpdateMovieCommand {

  PublicId publicId();
  Version version();
  Title title();
  ReleaseDate releaseDate();
  Score score();
  LanguageCode originalLanguage();
  Overview overview();
}
