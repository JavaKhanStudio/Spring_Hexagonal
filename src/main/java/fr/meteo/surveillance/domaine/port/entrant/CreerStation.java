package fr.meteo.surveillance.domaine.port.entrant;

import fr.meteo.surveillance.domaine.modele.Localisation;
import fr.meteo.surveillance.domaine.modele.StationId;

/**
 * PORT ENTRANT : cas d'usage "creer une station meteo".
 */
public interface CreerStation {

    StationId creer(Localisation localisation);
}
