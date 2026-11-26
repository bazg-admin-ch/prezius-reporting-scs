package ch.admin.bazg.prezius.reporting;

import ch.admin.bazg.prezius.reporting.adapter.in.rest.ui.reports.mapper.HallmarkingFilterCriteriaMapper;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.Architectures;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.validation.Constraint;
import org.apache.avro.specific.AvroGenerated;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "ch.admin.bazg.prezius.reporting..", importOptions = {ImportOption.DoNotIncludeTests.class})
@SuppressWarnings("java:S116")
class ArchitectureTest {

    private static final String DOMAIN_LAYER_PACKAGE = "..domain..";
    private static final String APPLICATION_LAYER_PACKAGE = "..application..";
    private static final String ADAPTER_LAYER_PACKAGE = "..adapter..";


    private static final String DOMAIN_LAYER_NAME = DOMAIN_LAYER_PACKAGE.replace("..", "").toUpperCase();
    private static final String APPLICATION_LAYER_NAME = APPLICATION_LAYER_PACKAGE.replace("..", "").toUpperCase();
    private static final String ADAPTER_LAYER_NAME = ADAPTER_LAYER_PACKAGE.replace("..", "").toUpperCase();

    private static final Architectures.LayeredArchitecture HEXAGONAL_ARCHITECTURE = Architectures.layeredArchitecture().consideringOnlyDependenciesInLayers()
                                                                                                 .layer(DOMAIN_LAYER_NAME)
                                                                                                 .definedBy(DOMAIN_LAYER_PACKAGE)
                                                                                                 .layer(APPLICATION_LAYER_NAME)
                                                                                                 .definedBy(APPLICATION_LAYER_PACKAGE)
                                                                                                 .layer(ADAPTER_LAYER_NAME)
                                                                                                 .definedBy(ADAPTER_LAYER_PACKAGE)
                                                                                                 // Add constraints
                                                                                                 .whereLayer(ADAPTER_LAYER_NAME)
                                                                                                 .mayNotBeAccessedByAnyLayer()
                                                                                                 .whereLayer(APPLICATION_LAYER_NAME)
                                                                                                 .mayOnlyBeAccessedByLayers(ADAPTER_LAYER_NAME)
                                                                                                 .whereLayer(DOMAIN_LAYER_NAME)
                                                                                                 .mayOnlyBeAccessedByLayers(APPLICATION_LAYER_NAME, ADAPTER_LAYER_NAME);

    @ArchTest
    private final ArchRule hexagonalArchitecture_layersAccessOnlyAllowedLayers = HEXAGONAL_ARCHITECTURE;


    // ---------- APPLICATION LAYER

    @ArchTest
    private final ArchRule applicationLayer_shouldNotHaveAvroOrRestDtoDependencies = noClasses()
            .that().resideInAPackage(APPLICATION_LAYER_PACKAGE)
            .should().dependOnClassesThat().areAnnotatedWith(AvroGenerated.class)
            .orShould().dependOnClassesThat().haveSimpleNameEndingWith("Dto");

    @ArchTest
    private final ArchRule applicationLayer_inputPortsShouldEndWithUseCaseOrQuery = classes()
            .that()
            .resideInAnyPackage("..application.ports.in..")
            .and().areNotAnnotatedWith(Constraint.class)
            .and().areInterfaces()
            .should().haveSimpleNameEndingWith("UseCase")
            .orShould().haveSimpleNameEndingWith("Query");

    @ArchTest
    private final ArchRule applicationLayer_inputPortsShouldBeInterfaces = classes()
            .that().resideInAnyPackage("..application.ports.in..")
            .and().areTopLevelClasses()
            .should().beInterfaces();

    @ArchTest final ArchRule applicationLayer_implementationClassesShouldBePackagePrivate = classes()
            .that().resideInAPackage("..application.implementation..")
            .and().areTopLevelClasses()
            .should().bePackagePrivate();

    @ArchTest
    private final ArchRule applicationLayer_outputPortsShouldEndWithPort = classes()
            .that()
            .resideInAnyPackage("..application.ports.out..")
            .and().areInterfaces()
            .should().haveSimpleNameEndingWith("Port");

    @ArchTest
    private final ArchRule applicationLayer_outputPortsShouldBeInterfaces = classes()
            .that().resideInAnyPackage("..application.ports.out..")
            .and().areTopLevelClasses()
            .should().beInterfaces();

    // ---------- ADAPTER LAYER

    @ArchTest
    private final ArchRule adapterLayer_shouldNotCallServicesOrImplementationsDirectly = noClasses()
            .that().resideInAPackage(ADAPTER_LAYER_PACKAGE)
            .should()
            .dependOnClassesThat()
            .resideInAnyPackage("..application.implementation..", "..application.services..");

    @ArchTest
    private final ArchRule adapterLayer_kafkaConsumerClassesShouldResideInKafkaConsumerPackage = methods()
            .that().areAnnotatedWith(KafkaListener.class)
            .should().beDeclaredInClassesThat().resideInAPackage("..adapter.in.kafka..")
            .allowEmptyShould(true); // remove as soon as kafka consumers exist

    @ArchTest
    private final ArchRule adapterLayer_restControllerClassesShouldResideInRestPackage
            = classes().that().areAnnotatedWith(Controller.class)
                       .or().areAnnotatedWith(RestController.class)
                       .and().resideOutsideOfPackage("..adapter.in.frontendconfig..")
                       .should().resideInAPackage("..adapter.in.rest..")
                       .andShould().haveSimpleNameEndingWith("RestController");

    @ArchTest final ArchRule adapterLayer_adapterClassesShouldBePackagePrivate = classes()
            .that().resideInAPackage(ADAPTER_LAYER_PACKAGE)
            .and().areNotAssignableTo(JpaRepository.class)
            .and().areNotAnnotatedWith(Repository.class)
            .and().areTopLevelClasses()
            .and().haveSimpleNameNotEndingWith(HallmarkingFilterCriteriaMapper.class.getSimpleName())
            .and().haveSimpleNameNotEndingWith("DtoBuilder")
            .should().bePackagePrivate();

    // ---------- DOMAIN LAYER

    @ArchTest
    private final ArchRule domainEntitiesShouldNotUsePublicSetters
            = methods()
            .that().areDeclaredInClassesThat().resideInAPackage(DOMAIN_LAYER_PACKAGE)
            .and().areDeclaredInClassesThat().areAnnotatedWith(Entity.class)
            .or().areDeclaredInClassesThat().areAnnotatedWith(Embeddable.class)
            .and().haveNameStartingWith("set")
            .should().notBePublic()
            .allowEmptyShould(true);

    @ArchTest
    private final ArchRule apiUi_shouldOnlyBeUsedFrom_adapterInRestUi = noClasses()
            .that().resideOutsideOfPackages(
                    "..adapter.in.rest.ui..",    // allowed packages
                    "..prezius.reporting.api.ui.." // allow intra-package usage
                                           )
            .should().dependOnClassesThat().resideInAnyPackage("..prezius.reporting.api.ui..");

}
