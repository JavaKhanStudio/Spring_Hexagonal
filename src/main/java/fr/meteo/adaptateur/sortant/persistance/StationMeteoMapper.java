package fr.meteo.adaptateur.sortant.persistance;

import fr.meteo.domaine.modele.Localisation;
import fr.meteo.domaine.modele.ReleveTemperature;
import fr.meteo.domaine.modele.StationId;
import fr.meteo.domaine.modele.StationMeteo;
import fr.meteo.domaine.modele.Temperature;

import java.util.List;

/**
 * MAPPER : traduit entre le modele de DOMAINE et le modele de PERSISTANCE.
 *
 * <p>C'est la piece qui permet aux deux modeles de rester independants. Le
 * domaine ne connait pas JPA ; JPA ne connait pas les regles du domaine ; le
 * mapper fait le pont, dans un seul sens de dependance (l'adaptateur depend du
 * domaine, jamais l'inverse).</p>
 */
final class StationMeteoMapper {

    private StationMeteoMapper() {
    }

    /** Domaine -> Persistance (avant sauvegarde). */
    static StationMeteoJpa versJpa(StationMeteo station) {
        List<ReleveTemperatureJpa> relevesJpa = station.releves().stream()
                .map(StationMeteoMapper::releveVersJpa)
                .toList();
        return new StationMeteoJpa(
                station.id().valeur(),
                station.localisation().ville(),
                station.localisation().codePostal(),
                relevesJpa);
    }

    /** Persistance -> Domaine (apres chargement). Reconstitue un agregat valide. */
    static StationMeteo versDomaine(StationMeteoJpa jpa) {
        List<ReleveTemperature> releves = jpa.getReleves().stream()
                .map(StationMeteoMapper::releveVersDomaine)
                .toList();
        return StationMeteo.reconstituer(
                new StationId(jpa.getId()),
                new Localisation(jpa.getVille(), jpa.getCodePostal()),
                releves);
    }

    private static ReleveTemperatureJpa releveVersJpa(ReleveTemperature releve) {
        Temperature t = releve.temperature();
        return new ReleveTemperatureJpa(releve.jour(), t.valeur(), t.unite());
    }

    private static ReleveTemperature releveVersDomaine(ReleveTemperatureJpa jpa) {
        return new ReleveTemperature(jpa.getJour(), Temperature.de(jpa.getValeur(), jpa.getUnite()));
    }
}
