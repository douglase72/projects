package com.erdouglass.emdb.test.scraper.movie;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;

import com.erdouglass.emdb.scraper.service.TmdbMovieScraper;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class AustinPowersGoldmemberIngestIT {
  
  @Inject
  TmdbMovieScraper scraper;

  @Test
  void testIngest() {
    scraper.onMessage();
  }

}
