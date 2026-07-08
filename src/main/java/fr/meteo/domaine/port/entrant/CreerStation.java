package fr.meteo.domaine.port.entrant;

import fr.meteo.domaine.modele.Localisation;
import fr.meteo.domaine.modele.StationId;

/**
 * PORT ENTRANT : cas d'usage "creer une station meteo".
 */
public interface CreerStation {

    StationId creer(Localisation localisation);
}
