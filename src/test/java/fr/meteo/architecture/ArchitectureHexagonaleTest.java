package fr.meteo.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * TEST D'ARCHITECTURE (ArchUnit).
 *
 * <p>Ces tests transforment les regles de l'architecture hexagonale en
 * assertions EXECUTABLES. Si un jour quelqu'un importe Spring dans le domaine,
 * la construction echoue. La documentation ne peut pas mentir : elle est
 * verifiee a chaque build.</p>
 */
class ArchitectureHexagonaleTest {

    private static JavaClasses classes;

    @BeforeAll
    static void importer() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("fr.meteo");
    }

    @Test
    void le_domaine_ne_depend_d_aucun_framework() {
        ArchRule regle = noClasses()
                .that().resideInAPackage("fr.meteo.domaine..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "jakarta.validation..",
                        "com.fasterxml..")
                .because("le coeur metier doit rester du Java pur, independant de toute technologie");

        regle.check(classes);
    }

    @Test
    void l_application_ne_depend_d_aucun_framework() {
        ArchRule regle = noClasses()
                .that().resideInAPackage("fr.meteo.application..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "org.springframework..",
                        "jakarta.persistence..",
                        "com.fasterxml..")
                .because("la couche application orchestre le domaine sans dependre du framework");

        regle.check(classes);
    }

    @Test
    void le_domaine_ne_depend_ni_de_l_application_ni_des_adaptateurs() {
        ArchRule regle = noClasses()
                .that().resideInAPackage("fr.meteo.domaine..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "fr.meteo.application..",
                        "fr.meteo.adaptateur..",
                        "fr.meteo.config..")
                .because("les dependances pointent vers l'interieur : le domaine ne connait personne");

        regle.check(classes);
    }

    @Test
    void les_adaptateurs_ne_se_referencent_pas_entre_eux() {
        ArchRule regle = noClasses()
                .that().resideInAPackage("fr.meteo.adaptateur.entrant..")
                .should().dependOnClassesThat().resideInAPackage("fr.meteo.adaptateur.sortant..")
                .because("un adaptateur entrant passe par les ports, jamais directement par un adaptateur sortant");

        regle.check(classes);
    }
}
