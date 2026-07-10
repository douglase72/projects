package com.erdouglass.emdb.media;

import static com.tngtech.archunit.base.DescribedPredicate.alwaysTrue;
import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAPackage;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.resideInAnyPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import java.util.regex.Pattern;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
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
  private static final DescribedPredicate<JavaClass> GRAPHQL_NONNULL =
      DescribedPredicate.describe("GraphQL @NonNull annotation",
          c -> c.getName().equals("org.eclipse.microprofile.graphql.NonNull"));  
  
  /** Coarse direction: adapter → application → domain. Dependencies outside the
   *  three layers (the api module, jakarta.*) are deliberately out of scope here —
   *  domain → api is legal (ShowStatus, SaveMovie), finer rules handle the rest. */
  @ArchTest
  static final ArchRule layers = layeredArchitecture()
      .consideringOnlyDependenciesInLayers()
      .layer("Adapter").definedBy("..media.adapter..")
      .layer("Application").definedBy("..media.application..")
      .layer("Domain").definedBy("..media.domain..")
      .whereLayer("Adapter").mayNotBeAccessedByAnyLayer()
      .whereLayer("Application").mayOnlyBeAccessedByLayers("Adapter")
      .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application");
  
  @ArchTest
  static final ArchRule domain_purity = noClasses()
      .that().resideInAPackage("..media.domain..")
      .should().dependOnClassesThat(
          resideInAnyPackage("org.hibernate..", "io.quarkus..", "com.fasterxml.jackson..",
                             "..media.application..", "..media.adapter..")
              .and(not(resideInAPackage("org.hibernate.annotations.."))));
  
  /** Adapters drive through ports: MovieResource injects MovieCommandService,
   *  never MovieService, and never touches an aggregate — Views exist so it
   *  doesn't have to. */
  @ArchTest
  static final ArchRule adapters_use_ports_only = noClasses()
      .that().resideInAPackage("..media.adapter..")
      .should().dependOnClassesThat().resideInAnyPackage(
          "..media.application.service..", "..media.domain..");
  
  /** Aggregates reference each other by id (PersonId from the api module), never
   *  by object reference. Adding @ManyToOne Person to MovieCredit fails the build.
   *  Everyone may lean on domain.shared (MediaEntity, ExternalId); shared leans
   *  on no aggregate — the un-ignored direction still catches that. */
  @ArchTest
  static final ArchRule aggregates_are_isolated = slices()
      .matching("..media.domain.(*)..").namingSlices("aggregate '$1'")
      .should().notDependOnEachOther()
      .ignoreDependency(alwaysTrue(), resideInAPackage("..media.domain.shared.."));

  @ArchTest
  static final ArchRule repositories_are_domain_interfaces = classes()
      .that().areMetaAnnotatedWith("jakarta.data.repository.Repository")
      .or().areAnnotatedWith("jakarta.data.repository.Repository")
      .should().beInterfaces()
      .andShould().resideInAPackage("..media.domain..");

  @ArchTest
  static final ArchRule ports_hold_no_beans = noClasses()
      .that().resideInAPackage("..media.application.port..")
      .should().beAnnotatedWith("jakarta.enterprise.context.ApplicationScoped")
      .orShould().haveSimpleNameEndingWith("Impl");
  
  @ArchTest
  static final ArchRule web_tech_stays_in_adapters = noClasses()
      .that().resideOutsideOfPackage("..media.adapter..")
      .should().dependOnClassesThat(
          resideInAnyPackage("jakarta.ws.rs..", "org.eclipse.microprofile.graphql..")
              .and(not(GRAPHQL_NONNULL)));
  
  public static class ExcludeGenerated implements ImportOption {
    private static final Pattern GENERATED = Pattern.compile(".*_\\.class");
    @Override public boolean includes(Location location) {
        return !location.matches(GENERATED);
    }
  }
}
