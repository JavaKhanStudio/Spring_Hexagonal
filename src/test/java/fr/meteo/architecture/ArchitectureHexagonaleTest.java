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
 * <p>Ces tests transforment les regles de l'architecture en assertions
 * EXECUTABLES. Si un jour quelqu'un importe Spring dans un domaine, ou fait
 * fuiter un contexte delimite dans l'autre, la construction echoue. La
 * documentation ne peut pas mentir : elle est verifiee a chaque build.</p>
 *
 * <p>Cette classe vit dans {@code fr.meteo.architecture}, en dehors des deux
 * contextes : elle les observe tous les deux, sans appartenir a aucun.</p>
 *
 * <p>Les regles se lisent en deux familles :</p>
 * <ul>
 *   <li><b>Regle de dependance</b> (interne a un contexte) : les fleches
 *       pointent vers le domaine.</li>
 *   <li><b>Frontiere de contexte</b> (entre contextes) : {@code surveillance} et
 *       {@code maintenance} s'ignorent, dans les deux sens.</li>
 * </ul>
 */
class ArchitectureHexagonaleTest {

    private static final String SURVEILLANCE = "fr.meteo.surveillance..";
    private static final String MAINTENANCE = "fr.meteo.maintenance..";
    private static final String RACINE_COMPOSITION = "fr.meteo.config..";

    private static final String[] FRAMEWORKS = {
            "org.springframework..",
            "jakarta.persistence..",
            "jakarta.validation..",
            "com.fasterxml.."
    };

    private static JavaClasses classes;

    @BeforeAll
    static void importer() {
        classes = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("fr.meteo");
    }

    // ----------------------------------------------------------------
    // Regle de dependance : a l'interieur d'un contexte
    // ----------------------------------------------------------------

    /** Le coeur des DEUX contextes est du Java pur. */
    @Test
    void aucun_domaine_ne_depend_d_un_framework() {
        ArchRule regle = noClasses()
                .that().resideInAnyPackage(
                        "fr.meteo.surveillance.domaine..",
                        "fr.meteo.maintenance.domaine..")
                .should().dependOnClassesThat().resideInAnyPackage(FRAMEWORKS)
                .because("le coeur metier de chaque contexte doit rester du Java pur, "
                        + "independant de toute technologie");

        regle.check(classes);
    }

    /** La couche application des DEUX contextes est du Java pur. */
    @Test
    void aucune_application_ne_depend_d_un_framework() {
        ArchRule regle = noClasses()
                .that().resideInAnyPackage(
                        "fr.meteo.surveillance.application..",
                        "fr.meteo.maintenance.application..")
                .should().dependOnClassesThat().resideInAnyPackage(FRAMEWORKS)
                .because("la couche application orchestre le domaine sans dependre du framework");

        regle.check(classes);
    }

    /** Dans chaque contexte, les fleches pointent vers le domaine. */
    @Test
    void aucun_domaine_ne_depend_de_son_application_ni_de_ses_adaptateurs() {
        ArchRule regle = noClasses()
                .that().resideInAnyPackage(
                        "fr.meteo.surveillance.domaine..",
                        "fr.meteo.maintenance.domaine..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "fr.meteo.surveillance.application..",
                        "fr.meteo.surveillance.adaptateur..",
                        "fr.meteo.maintenance.application..",
                        "fr.meteo.maintenance.adaptateur..")
                .because("les dependances pointent vers l'interieur : le domaine ne connait personne");

        regle.check(classes);
    }

    @Test
    void les_adaptateurs_ne_se_referencent_pas_entre_eux() {
        ArchRule regle = noClasses()
                .that().resideInAnyPackage(
                        "fr.meteo.surveillance.adaptateur.entrant..",
                        "fr.meteo.maintenance.adaptateur.entrant..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        "fr.meteo.surveillance.adaptateur.sortant..",
                        "fr.meteo.maintenance.adaptateur.sortant..")
                .because("un adaptateur entrant passe par les ports, jamais directement "
                        + "par un adaptateur sortant");

        regle.check(classes);
    }

    // ----------------------------------------------------------------
    // Frontiere de contexte delimite : entre contextes
    // ----------------------------------------------------------------

    /**
     * Le contexte "maintenance" possede son propre modele de station, et jusqu'a
     * son propre {@code StationId}. Cette regle interdit qu'il aille emprunter
     * une classe au contexte "surveillance" : les deux modeles doivent pouvoir
     * evoluer separement. Ils ne partagent que la valeur brute d'un UUID.
     */
    @Test
    void le_contexte_maintenance_ignore_le_contexte_surveillance() {
        ArchRule regle = noClasses()
                .that().resideInAPackage(MAINTENANCE)
                .should().dependOnClassesThat().resideInAPackage(SURVEILLANCE)
                .because("un contexte delimite ne partage pas son modele : "
                        + "la duplication de StationId entre contextes est deliberee");

        regle.check(classes);
    }

    /** La frontiere vaut dans les deux sens : aucun contexte n'est le "principal". */
    @Test
    void le_contexte_surveillance_ignore_le_contexte_maintenance() {
        ArchRule regle = noClasses()
                .that().resideInAPackage(SURVEILLANCE)
                .should().dependOnClassesThat().resideInAPackage(MAINTENANCE)
                .because("le detecteur de canicules n'a aucune raison de savoir "
                        + "ce qu'est un etalonnage");

        regle.check(classes);
    }

    /**
     * La racine de composition ({@code fr.meteo.config}) a le droit de connaitre
     * les deux contextes, pour les instancier. L'inverse est interdit : un
     * contexte ignore qui l'assemble, et ignore donc Spring.
     */
    @Test
    void aucun_contexte_ne_depend_de_la_racine_de_composition() {
        ArchRule regle = noClasses()
                .that().resideInAnyPackage(SURVEILLANCE, MAINTENANCE)
                .should().dependOnClassesThat().resideInAPackage(RACINE_COMPOSITION)
                .because("la racine de composition connait les contextes, jamais l'inverse");

        regle.check(classes);
    }
}
