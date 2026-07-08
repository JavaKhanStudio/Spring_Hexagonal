package fr.meteo.domaine.modele;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * RACINE D'AGREGAT (Aggregate Root).
 *
 * <p>Une station meteo regroupe une localisation et l'ensemble de ses releves.
 * C'est la <b>frontiere de coherence</b> du domaine : on ne modifie jamais un
 * releve directement de l'exterieur, on passe toujours par la station qui
 * garantit ses invariants.</p>
 *
 * <p>Invariant protege ici : <b>un seul releve par jour</b>. Toute tentative
 * d'en ajouter un deuxieme pour la meme date est rejetee. C'est exactement le
 * role d'un agregat : etre le gardien d'une regle metier.</p>
 *
 * <p>La liste des releves est encapsulee : l'exterieur en obtient une copie non
 * modifiable ({@link #releves()}), ce qui empeche de contourner l'invariant.</p>
 */
public class StationMeteo {

    private final StationId id;
    private final Localisation localisation;
    private final List<ReleveTemperature> releves;

    public StationMeteo(StationId id, Localisation localisation) {
        this.id = Objects.requireNonNull(id, "L'identifiant est obligatoire");
        this.localisation = Objects.requireNonNull(localisation, "La localisation est obligatoire");
        this.releves = new ArrayList<>();
    }

    /**
     * Reconstruit une station a partir de donnees existantes (utilise par
     * l'adaptateur de persistance). Les invariants sont revalides au passage.
     */
    public static StationMeteo reconstituer(StationId id, Localisation localisation,
                                            List<ReleveTemperature> relevesExistants) {
        StationMeteo station = new StationMeteo(id, localisation);
        relevesExistants.forEach(station::enregistrer);
        return station;
    }

    /**
     * Enregistre un nouveau releve en protegeant l'invariant "un releve par jour".
     *
     * @throws ReleveDejaPresentException si un releve existe deja pour ce jour
     */
    public void enregistrer(ReleveTemperature releve) {
        Objects.requireNonNull(releve, "Le releve est obligatoire");
        boolean jourDejaConnu = releves.stream()
                .anyMatch(r -> r.jour().equals(releve.jour()));
        if (jourDejaConnu) {
            throw new ReleveDejaPresentException(id, releve.jour());
        }
        releves.add(releve);
    }

    /** Les releves tries par date croissante (copie non modifiable). */
    public List<ReleveTemperature> releves() {
        return releves.stream()
                .sorted(Comparator.comparing(ReleveTemperature::jour))
                .toList();
    }

    public StationId id() {
        return id;
    }

    public Localisation localisation() {
        return localisation;
    }

    /** Deux stations sont "la meme" si elles partagent la meme identite. */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StationMeteo autre)) {
            return false;
        }
        return id.equals(autre.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Exception metier levee quand l'invariant "un releve par jour" est viole.
     * Elle vit dans le domaine : c'est un evenement du langage metier, pas une
     * erreur technique.
     */
    public static class ReleveDejaPresentException extends RuntimeException {
        public ReleveDejaPresentException(StationId station, LocalDate jour) {
            super("Un releve existe deja pour la station " + station
                    + " le " + jour);
        }
    }
}
