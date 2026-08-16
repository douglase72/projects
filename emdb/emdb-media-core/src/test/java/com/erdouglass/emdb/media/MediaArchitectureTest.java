package com.erdouglass.emdb.media;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import java.util.regex.Pattern;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.core.importer.Location;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(
    packages = "com.erdouglass.emdb.media",
    importOptions = { ImportOption.DoNotIncludeTests.class,
                      MediaArchitectureTest.ExcludeGenerated.class })
class MediaArchitectureTest {

  /// Package-private services turn "adapters depend on ports, never the
  /// service" into a compiler guarantee; this keeps anyone from ever
  /// loosening it.
  @ArchTest
  static final ArchRule services_hide_behind_ports = classes()
      .that().resideInAPackage("..media.movie.application.service..")
      .should().notBePublic();  
  
  public static class ExcludeGenerated implements ImportOption {
    private static final Pattern GENERATED = Pattern.compile(".*_\\.class");
    @Override public boolean includes(Location location) {
        return !location.matches(GENERATED);
    }
  }
}
