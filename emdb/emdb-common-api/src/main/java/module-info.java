module com.erdouglass.emdb.common.api {
  requires transitive jakarta.validation;
  requires transitive com.erdouglass.common.validation;
  requires transitive com.fasterxml.jackson.annotation;
  
  exports com.erdouglass.emdb.common;
  exports com.erdouglass.emdb.common.movie;
  exports com.erdouglass.emdb.common.series;
}