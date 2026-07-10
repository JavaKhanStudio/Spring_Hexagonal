package fr.meteo.surveillance.adaptateur.sortant.persistance;

import fr.meteo.surveillance.domaine.modele.Canicule;
import fr.meteo.surveillance.domaine.modele.CaniculeId;
import fr.meteo.surveillance.domaine.modele.Localisation;
import fr.meteo.surveillance.domaine.modele.StationId;
import fr.meteo.surveillance.domaine.modele.Temperature;

/**
 * MAPPER canicule : domaine <-> persistance.
 */
final class CaniculeMapper {

    private CaniculeMapper() {
    }

    static CaniculeJpa versJpa(Canicule canicule) {
        Temperature pic = canicule.temperaturePic();
        return new CaniculeJpa(
                canicule.id().valeur(),
                canicule.stationId().valeur(),
                canicule.localisation().ville(),
                canicule.localisation().codePostal(),
                canicule.dateDebut(),
                canicule.dateFin(),
                pic.valeur(),
                pic.unite());
    }

    static Canicule versDomaine(CaniculeJpa jpa) {
        return Canicule.reconstituer(
                new CaniculeId(jpa.getId()),
                new StationId(jpa.getStationId()),
                new Localisation(jpa.getVille(), jpa.getCodePostal()),
                jpa.getDateDebut(),
                jpa.getDateFin(),
                Temperature.de(jpa.getPicValeur(), jpa.getPicUnite()));
    }
}
