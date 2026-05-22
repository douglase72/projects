module com.erdouglass.common.validation {
  requires jakarta.cdi;
  requires jakarta.inject;
  requires transitive jakarta.validation;
  
  exports com.erdouglass.common.validation;
}