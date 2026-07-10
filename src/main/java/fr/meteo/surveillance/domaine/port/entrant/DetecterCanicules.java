package fr.meteo.surveillance.domaine.port.entrant;

import fr.meteo.surveillance.domaine.modele.Canicule;
import fr.meteo.surveillance.domaine.modele.StationId;

import java.util.List;

/**
 * PORT ENTRANT : cas d'usage "detecter les canicules d'une station".
 *
 * <p>Declenche l'analyse des releves d'une station selon le seuil metier,
 * enregistre les canicules trouvees et notifie. Renvoie les canicules detectees.</p>
 */
public interface DetecterCanicules {

    List<Canicule> detecterPour(StationId stationId);
}
