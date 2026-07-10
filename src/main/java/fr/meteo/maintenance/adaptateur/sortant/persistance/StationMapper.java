package fr.meteo.maintenance.adaptateur.sortant.persistance;

import fr.meteo.maintenance.domaine.modele.NumeroSerie;
import fr.meteo.maintenance.domaine.modele.Station;
import fr.meteo.maintenance.domaine.modele.StationId;

/**
 * MAPPER du contexte maintenance : domaine &lt;-&gt; persistance.
 *
 * <p>Notez ce que ce mapper NE peut pas faire : produire une
 * {@code fr.meteo.surveillance.domaine.modele.StationMeteo}. Il lui manquerait
 * les releves, et il devrait jeter le numero de serie. Un mapper traduit entre
 * deux <b>representations d'un meme modele</b> ; il ne traverse jamais une
 * frontiere de contexte.</p>
 */
final class StationMapper {

    private StationMapper() {
    }

    /** Domaine -> Persistance. */
    static StationJpa versJpa(Station station) {
        return new StationJpa(
                station.id().valeur(),
                station.numeroSerie().valeur(),
                station.etat(),
                station.dernierEtalonnage());
    }

    /** Persistance -> Domaine. Passe par la fabrique : les invariants sont revalides. */
    static Station versDomaine(StationJpa jpa) {
        return Station.reconstituer(
                new StationId(jpa.getId()),
                new NumeroSerie(jpa.getNumeroSerie()),
                jpa.getDernierEtalonnage(),
                jpa.getEtat());
    }
}
