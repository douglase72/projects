package com.erdouglass.emdb.media;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.dependencies.SlicesRuleDefinition;

@AnalyzeClasses(
    packages = ArchitectureTest.ROOT,
    importOptions = { ImportOption.DoNotIncludeTests.class, ImportOption.DoNotIncludeJars.class })
class ArchitectureTest {

  static final String ROOT = "com.erdouglass.emdb";

  private static final String KERNEL = "com.erdouglass.emdb.media.kernel..";
  private static final String DOMAIN = "com.erdouglass.emdb.media..domain.model..";
  private static final String PORTS = "com.erdouglass.emdb.media..application.port..";
  private static final String PORTS_IN = "com.erdouglass.emdb.media..application.port.in..";
  private static final String PORTS_OUT = "com.erdouglass.emdb.media..application.port.out..";
  private static final String SERVICES = "com.erdouglass.emdb.media..application.service..";
  private static final String ADAPTERS = "com.erdouglass.emdb.media..adapter..";
  private static final String ADAPTERS_IN = "com.erdouglass.emdb.media..adapter.in..";
  private static final String ADAPTERS_OUT = "com.erdouglass.emdb.media..adapter.out..";

  private static final String[] FRAMEWORKS = {
    "jakarta..", "javax..", "org.hibernate..", "io.quarkus..", "org.jboss..",
    "org.eclipse.microprofile..", "com.fasterxml.jackson..", "io.smallrye.."
  };

  // ---------------------------------------------------------------------------
  // The shape of the hexagon
  // ---------------------------------------------------------------------------

  /// The whole dependency rule in one assertion: every arrow points inwards.
  ///
  /// `consideringOnlyDependenciesInLayers` ignores everything outside these
  /// packages — the JDK, the frameworks — so the rule speaks only about our own
  /// code. Framework leakage is handled separately below.
  @ArchTest
  static final ArchRule layers_point_inwards = layeredArchitecture()
      .consideringOnlyDependenciesInLayers()
      .layer("Kernel").definedBy(KERNEL)
      .layer("Domain").definedBy(DOMAIN)
      .layer("Ports").definedBy(PORTS)
      .layer("Services").definedBy(SERVICES)
      .layer("Adapters").definedBy(ADAPTERS)

      .whereLayer("Adapters").mayNotBeAccessedByAnyLayer()
      .whereLayer("Services").mayNotBeAccessedByAnyLayer()
      .whereLayer("Ports").mayOnlyBeAccessedByLayers("Services", "Adapters")
      .whereLayer("Domain").mayOnlyBeAccessedByLayers("Ports", "Services", "Adapters")
      .whereLayer("Kernel").mayOnlyBeAccessedByLayers("Domain", "Ports", "Services", "Adapters")

      .because("an inward dependency can be tested and reasoned about in isolation; "
          + "an outward one drags the database and the HTTP layer into every test");
  
  /// Inbound and outbound adapters must not know about each other.
  ///
  /// Covered in aggregate by the layered rule, but stated separately because the
  /// failure message is far more useful — a REST resource reaching for a JPA
  /// entity is a specific mistake with a specific fix.
  @ArchTest
  static final ArchRule adapters_do_not_talk_to_each_other = noClasses()
      .that().resideInAPackage(ADAPTERS_IN)
      .should().dependOnClassesThat().resideInAPackage(ADAPTERS_OUT)
      .because("the two sides meet at a port, not at each other; otherwise swapping "
          + "the persistence adapter means editing the REST layer");

  /// The domain must not reach outwards, in either direction.
  @ArchTest
  static final ArchRule domain_depends_on_nothing_outside_itself = noClasses()
      .that().resideInAPackage(DOMAIN)
      .should().dependOnClassesThat().resideInAnyPackage(PORTS, SERVICES, ADAPTERS)
      .because("the aggregate defines the rules; it cannot also depend on the code "
          + "that invokes them without becoming untestable and circular");
  
  /// Ports are the boundary, so they may not name anything on the far side of it.
  @ArchTest
  static final ArchRule ports_do_not_depend_on_adapters = noClasses()
      .that().resideInAPackage(PORTS)
      .should().dependOnClassesThat().resideInAPackage(ADAPTERS)
      .because("a port that names an adapter is not a port — it is a second copy "
          + "of the adapter's API with no way to substitute an implementation");

  @ArchTest
  static final ArchRule no_cycles_between_slices = SlicesRuleDefinition.slices()
      .matching("com.erdouglass.emdb.media.movie.(*)..")
      .should().beFreeOfCycles()
      .because("a cycle between domain, application and adapter means none of the "
          + "three can be understood, tested or replaced on its own");

  // ---------------------------------------------------------------------------
  // Domain purity
  // ---------------------------------------------------------------------------

  /// No framework types in the domain or the kernel.
  ///
  /// The practical test of this rule is whether the aggregate can be exercised in
  /// a plain unit test with no container, no datasource and no HTTP. Once a
  /// persistence or validation annotation appears, the domain's shape starts
  /// being negotiated with a framework rather than with the business.
  @ArchTest
  static final ArchRule domain_is_framework_free = noClasses()
      .that().resideInAnyPackage(DOMAIN, KERNEL)
      .should().dependOnClassesThat().resideInAnyPackage(FRAMEWORKS)
      .because("the domain must be testable without a container, and its shape must "
          + "be driven by the business rather than by an ORM's requirements");

  /// Ports may carry commands and results, but not persistence or serialisation
  /// concerns.
  ///
  /// Deliberately narrower than the domain rule: validation annotations on a
  /// command are a defensible boundary choice, so `jakarta.validation` is not
  /// listed here.
  @ArchTest
  static final ArchRule ports_are_free_of_persistence_and_transport = noClasses()
      .that().resideInAPackage(PORTS)
      .should().dependOnClassesThat().resideInAnyPackage(
          "jakarta.persistence..", "jakarta.ws.rs..", "jakarta.json..",
          "org.hibernate..", "com.fasterxml..", "org.eclipse.microprofile.graphql..")
      .because("a port describes what the application can do, not how it is called "
          + "or where the data lands");

  /// Domain objects expose behaviour, not setters.
  ///
  /// A setter lets a caller move the aggregate between states the aggregate
  /// itself would never have produced. Name the transition instead — `lock`,
  /// `update`, `checkVersion` — so the rule that guards it has somewhere to live.
  @ArchTest
  static final ArchRule domain_exposes_no_setters = methods()
      .that().areDeclaredInClassesThat().resideInAnyPackage(DOMAIN, KERNEL)
      .and().arePublic()
      .should().haveNameNotMatching("set[A-Z].*")
      .because("state transitions belong behind a named operation that can enforce "
          + "an invariant, not behind an assignment");

  /// Value objects are immutable.
  @ArchTest
  static final ArchRule kernel_fields_are_final = fields()
      .that().areDeclaredInClassesThat().resideInAPackage(KERNEL)
      .and().areNotStatic()
      .should().beFinal()
      .because("a value object that can change is not a value; equality and the "
          + "invariants checked at construction both stop meaning anything");

  // ---------------------------------------------------------------------------
  // Ports and adapters
  // ---------------------------------------------------------------------------

  @ArchTest
  static final ArchRule inbound_ports_are_interfaces = classes()
      .that().resideInAPackage(PORTS_IN)
      .and().areTopLevelClasses()
      .and().haveSimpleNameEndingWith("UseCase")
      .should().beInterfaces()
      .andShould().bePublic()
      .because("the use case is the contract adapters compile against; a class "
          + "there is an implementation that has escaped the service package");

  @ArchTest
  static final ArchRule outbound_ports_are_interfaces = classes()
      .that().resideInAPackage(PORTS_OUT)
      .and().areTopLevelClasses()
      .and().haveSimpleNameEndingWith("Repository")
      .should().beInterfaces()
      .andShould().bePublic()
      .because("the application must be able to name its persistence needs without "
          + "naming an implementation of them");

  /// Every use case interface lives in `port.in` and nowhere else.
  ///
  /// The rule that catches ports drifting into a shared or top-level package,
  /// where they stop being distinguishable from plain DTOs.
  @ArchTest
  static final ArchRule use_cases_live_in_port_in = classes()
      .that().haveSimpleNameEndingWith("UseCase")
      .should().resideInAPackage(PORTS_IN)
      .because("scattering ports across packages makes the application's public "
          + "surface impossible to read off the package structure");

  /// Services implement ports and are invisible to everyone else.
  ///
  /// Package-private is what forces adapters to inject the interface. CDI
  /// resolves by type, so nothing is lost by hiding the class.
  @ArchTest
  static final ArchRule services_are_package_private = classes()
      .that().resideInAPackage(SERVICES)
      .and().areTopLevelClasses()
      .and().implement(JavaClass.Predicates.simpleNameEndingWith("UseCase"))
      .should().bePackagePrivate()
      .andShould().beAnnotatedWith("jakarta.enterprise.context.ApplicationScoped")
      .because("an adapter that can name the service class will eventually inject "
          + "it, and the port stops being a seam");

  /// The transaction boundary sits in exactly one layer.
  ///
  /// A `@Transactional` adapter widens the boundary past the point where the
  /// application decided a unit of work ends — which is how audit rows end up
  /// committed separately from the change they describe.
  @ArchTest
  static final ArchRule transactions_are_demarcated_by_services = classes()
      .that().containAnyMethodsThat(
          DescribedPredicate.<JavaMethod>describe(
              "are annotated with @Transactional",
              method -> method.isAnnotatedWith("jakarta.transaction.Transactional")))
      .should().resideInAPackage(SERVICES)
      .allowEmptyShould(true)
      .because("one layer owns the unit of work; two layers owning it means "
          + "nobody can say what commits together");

  // ---------------------------------------------------------------------------
  // Persistence stays behind the port
  // ---------------------------------------------------------------------------

  @ArchTest
  static final ArchRule entities_live_in_the_persistence_adapter = classes()
      .that().areAnnotatedWith("jakarta.persistence.Entity")
      .should().resideInAPackage(ADAPTERS_OUT)
      .andShould().bePackagePrivate()
      .because("an entity is a row, not a model; the moment one is visible outside "
          + "the adapter it starts being passed around as if it were the domain");
  
  @ArchTest
  static final ArchRule entities_are_not_referenced_outside_the_adapter = noClasses()
      .that().resideOutsideOfPackage(ADAPTERS_OUT)
      .should().dependOnClassesThat().haveSimpleNameEndingWith("Entity")
      .because("returning an entity across the port leaks lazy loading and the "
          + "persistence context into layers that cannot manage either");

  /// The aggregate is loaded and mutated by the application, never by a
  /// controller.
  ///
  /// Value objects are fine at the boundary — a resource parsing a
  /// `MoviePublicId` is the boundary doing its job. The aggregate is not.
  @ArchTest
  static final ArchRule inbound_adapters_do_not_touch_the_aggregate = noClasses()
      .that().resideInAPackage(ADAPTERS_IN)
      .should().dependOnClassesThat().haveFullyQualifiedName(
          "com.erdouglass.emdb.media.movie.domain.model.Movie")
      .because("a resource holding the aggregate will eventually apply a rule to it, "
          + "and that rule will not be enforced on the other transport");
  
  // ---------------------------------------------------------------------------
  // Aggregates
  // --------------------------------------------------------------------------- 
  @ArchTest
  static final ArchRule aggregates_reference_by_id_only = noClasses()
      .that().resideInAPackage("..movie.domain..")
      .should().dependOnClassesThat(
          JavaClass.Predicates.resideInAPackage("..person.domain..")
              .and(DescribedPredicate.not(
                  JavaClass.Predicates.simpleNameEndingWith("Id"))))
      .because("an object reference across aggregates puts two roots, two versions "
          + "and two lifecycles inside one transaction");
  
  // ---------------------------------------------------------------------------
  // General hygiene
  // ---------------------------------------------------------------------------

  @ArchTest
  static final ArchRule no_standard_streams = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

  @ArchTest
  static final ArchRule no_jul = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

  @ArchTest
  static final ArchRule no_field_injection_outside_adapters_and_services = noClasses()
      .that().resideOutsideOfPackages(ADAPTERS, SERVICES)
      .should().dependOnClassesThat().haveFullyQualifiedName("jakarta.inject.Inject")
      .because("only the two outermost layers are container-managed; anything else "
          + "needing injection has drifted out of the hexagon");
}
