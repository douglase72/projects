package com.erdouglass.emdb.media;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

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
  
  /// "No framework import may ever appear in this package" — Movie's javadoc,
  /// made mechanical. Whitelist, not blacklist: the domain may see itself and
  /// the JDK; everything else fails by default, no enumeration to keep current.
  @ArchTest
  static final ArchRule domain_speaks_only_domain_and_jdk = classes()
      .that().resideInAPackage("..media.domain..")
      .should().onlyDependOnClassesThat(
          resideInAnyPackage("..media.domain..", "..emdb.media..", "java.."));
  
  /// Unchanged. The asymmetric ignore is deliberate: anyone may lean on the
  /// shared kernel; the kernel may lean on no aggregate.
  @ArchTest
  static final ArchRule aggregates_are_isolated = slices()
      .matching("..media.domain.(*)..").namingSlices("aggregate '$1'")
      .should().notDependOnEachOther()
      .ignoreDependency(alwaysTrue(), resideInAnyPackage("..media.domain.shared..", "..media.domain.exception.."));
  
  /// The dependency-inversion seam: arrows point inward, so nothing inside
  /// the hexagon may ever name an adapter type.
  /*
  @ArchTest
  static final ArchRule hexagon_never_sees_an_adapter = noClasses()
      .that().resideOutsideOfPackage("..media.adapter..")
      .should().dependOnClassesThat().resideInAPackage("..media.adapter..");
  
  /// MovieResource's "never on anything in adapter.outbound", generalized:
  /// adapters compose through ports, never through each other.
  @ArchTest
  static final ArchRule adapters_do_not_know_each_other = slices()
      .matching("..media.adapter.(**)").namingSlices("adapter '$1'")
      .should().notDependOnEachOther();
  */
  
  /// "The only place on the write path where HTTP vocabulary may appear."
  /// Also the regression test for jakarta.ws.rs.NotFoundException escaping
  /// into the service.
  @ArchTest
  static final ArchRule http_stays_in_the_rest_adapter = noClasses()
      .that().resideOutsideOfPackage("..media.adapter.inbound..")
      .should().dependOnClassesThat().resideInAPackage("jakarta.ws.rs..");
  
  /// "No entity, SQL, or Jakarta Data type may appear in [port] signatures",
  /// widened to the whole vocabulary. This is also what pins the exception
  /// translation where we decided it goes: catching
  /// OptimisticLockingFailureException in the service would name jakarta.data
  /// and fail here — the adapter must translate to StaleVersionException.
  @ArchTest
  static final ArchRule persistence_stays_in_its_adapter = noClasses()
      .that().resideOutsideOfPackage("..media.adapter.outbound..")
      .should().dependOnClassesThat()
      .resideInAnyPackage("jakarta.persistence..", "jakarta.data..", "org.hibernate..");

  /// "@Transactional lives on the use-case methods and nowhere else." Same
  /// containment shape: transaction vocabulary is the service's dialect.
  @ArchTest
  static final ArchRule transactions_begin_in_the_service = noClasses()
      .that().resideOutsideOfPackage("..media.application.service..")
      .should().dependOnClassesThat().resideInAPackage("jakarta.transaction..");

  /// Package-private services turn "adapters depend on ports, never the
  /// service" into a compiler guarantee; this keeps anyone from ever
  /// loosening it.
  @ArchTest
  static final ArchRule services_hide_behind_ports = classes()
      .that().resideInAPackage("..media.application.service..")
      .should().notBePublic();  
  
  public static class ExcludeGenerated implements ImportOption {
    private static final Pattern GENERATED = Pattern.compile(".*_\\.class");
    @Override public boolean includes(Location location) {
        return !location.matches(GENERATED);
    }
  }
}
