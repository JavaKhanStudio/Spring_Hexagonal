package fr.meteo.domaine.port.sortant;

import fr.meteo.domaine.modele.Canicule;
import fr.meteo.domaine.modele.StationId;

import java.util.List;

/**
 * PORT SORTANT : le depot des canicules detectees.
 */
public interface DepotCanicule {

    void sauvegarder(Canicule canicule);

    List<Canicule> toutes();

    /** Les canicules deja connues pour une station (evite les doublons). */
    List<Canicule> parStation(StationId stationId);
}
