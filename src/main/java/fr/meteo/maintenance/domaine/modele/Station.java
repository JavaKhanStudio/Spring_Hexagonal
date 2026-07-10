package fr.meteo.maintenance.domaine.modele;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

/**
 * RACINE D'AGREGAT : une station, <b>vue par le contexte maintenance</b>.
 *
 * <p>Le meme objet du monde reel que {@code fr.meteo.surveillance.domaine.modele.StationMeteo},
 * et pourtant un modele entierement different :</p>
 * <ul>
 *   <li>elle ne connait <b>aucun releve de temperature</b> : ce n'est pas son sujet ;</li>
 *   <li>elle connait un numero de serie et une date d'etalonnage, dont le
 *       contexte surveillance n'a jamais entendu parler ;</li>
 *   <li>elle garde un <b>autre invariant</b> : une station dont l'etalonnage est
 *       perime ne peut pas etre mise en service.</li>
 * </ul>
 *
 * <p>Contraste utile avec {@code StationMeteo} : cette entite a un etat qui
 * <b>evolue</b> ({@link #etat}, {@link #dernierEtalonnage} changent au fil du
 * temps), alors que la station de surveillance ne fait qu'accumuler des releves.
 * Les deux sont des entites : leur identite survit aux changements d'etat.</p>
 */
public class Station {

    /** Duree de validite d'un etalonnage, imposee par la reglementation. */
    public static final int VALIDITE_ETALONNAGE_JOURS = 365;

    private final StationId id;
    private final NumeroSerie numeroSerie;
    private EtatOperationnel etat;
    private LocalDate dernierEtalonnage;

    public Station(StationId id, NumeroSerie numeroSerie, LocalDate dernierEtalonnage) {
        this.id = Objects.requireNonNull(id, "L'identifiant est obligatoire");
        this.numeroSerie = Objects.requireNonNull(numeroSerie, "Le numero de serie est obligatoire");
        this.dernierEtalonnage = Objects.requireNonNull(dernierEtalonnage, "La date d'etalonnage est obligatoire");
        this.etat = EtatOperationnel.HORS_SERVICE;
    }

    /**
     * Reconstruit une station depuis la persistance, en restaurant son etat.
     *
     * <p>Jumelle de {@code StationMeteo.reconstituer} dans l'autre contexte :
     * l'adaptateur ne peut pas ecrire directement dans les champs, il passe par
     * une fabrique du domaine.</p>
     */
    public static Station reconstituer(StationId id, NumeroSerie numeroSerie,
                                       LocalDate dernierEtalonnage, EtatOperationnel etat) {
        Station station = new Station(id, numeroSerie, dernierEtalonnage);
        station.etat = Objects.requireNonNull(etat, "L'etat est obligatoire");
        return station;
    }

    /**
     * Met la station en service.
     *
     * <p>Invariant garde par l'agregat : impossible de mettre en service un
     * equipement dont l'etalonnage a expire. Les mesures ne seraient pas fiables.</p>
     *
     * @throws EtalonnagePerimeException si l'etalonnage date de plus d'un an
     */
    public void mettreEnService(LocalDate aujourdHui) {
        if (etalonnagePerime(aujourdHui)) {
            throw new EtalonnagePerimeException(id, dernierEtalonnage);
        }
        this.etat = EtatOperationnel.EN_SERVICE;
    }

    /** Immobilise la station (panne, ou etalonnage expire constate). */
    public void mettreHorsService() {
        this.etat = EtatOperationnel.HORS_SERVICE;
    }

    /** Envoie la station au banc de calibration. */
    public void demarrerEtalonnage() {
        this.etat = EtatOperationnel.EN_ETALONNAGE;
    }

    /**
     * Enregistre un etalonnage reussi. La station redevient exploitable.
     *
     * @throws IllegalArgumentException si la date est dans le futur
     */
    public void etalonner(LocalDate jour, LocalDate aujourdHui) {
        Objects.requireNonNull(jour, "La date d'etalonnage est obligatoire");
        if (jour.isAfter(aujourdHui)) {
            throw new IllegalArgumentException("Un etalonnage ne peut pas etre date du futur : " + jour);
        }
        this.dernierEtalonnage = jour;
        this.etat = EtatOperationnel.EN_SERVICE;
    }

    /** {@code true} si l'etalonnage a depasse sa duree de validite. */
    public boolean etalonnagePerime(LocalDate aujourdHui) {
        return ChronoUnit.DAYS.between(dernierEtalonnage, aujourdHui) > VALIDITE_ETALONNAGE_JOURS;
    }

    public StationId id() {
        return id;
    }

    public NumeroSerie numeroSerie() {
        return numeroSerie;
    }

    public EtatOperationnel etat() {
        return etat;
    }

    public LocalDate dernierEtalonnage() {
        return dernierEtalonnage;
    }

    /** Entite : l'identite, et elle seule, definit l'egalite. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof Station autre && id.equals(autre.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Exception metier du contexte maintenance. Son vocabulaire ("etalonnage
     * perime") n'aurait aucun sens dans le contexte surveillance.
     */
    public static class EtalonnagePerimeException extends RuntimeException {
        public EtalonnagePerimeException(StationId station, LocalDate dernierEtalonnage) {
            super("La station " + station + " ne peut pas etre mise en service : "
                    + "dernier etalonnage le " + dernierEtalonnage);
        }
    }
}
