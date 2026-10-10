package com.erdouglass.emdb.scraper.adapter.out.messaging;

import com.erdouglass.emdb.scraper.application.port.out.MovieScraper;
import com.erdouglass.emdb.scraper.domain.event.MovieExtracted;

interface MovieMapper {

  MovieScraper toMovieScraper(MovieExtracted event);
}
