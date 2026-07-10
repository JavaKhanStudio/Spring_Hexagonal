package fr.meteo.surveillance.domaine.port.entrant;

import fr.meteo.surveillance.domaine.modele.ReleveTemperature;
import fr.meteo.surveillance.domaine.modele.StationId;

/**
 * PORT ENTRANT (driving port) : cas d'usage "enregistrer un releve".
 *
 * <p>Un port entrant est une interface que le <b>monde exterieur</b> (ici un
 * controleur REST) appelle pour piloter l'application. Il est defini par le
 * domaine, en vocabulaire metier, sans rien connaitre de HTTP ni de JSON.</p>
 *
 * <p>Cette inversion est le coeur de l'architecture hexagonale : c'est
 * l'application qui dicte son interface, pas la technologie d'entree.</p>
 */
public interface EnregistrerReleve {

    /**
     * Enregistre un releve de temperature sur une station.
     *
     * @return l'identifiant de la station concernee
     */
    StationId enregistrer(Commande commande);

    /**
     * Objet de commande : les donnees d'entree du cas d'usage, exprimees dans
     * le langage du domaine (et non un DTO web).
     */
    record Commande(StationId stationId, ReleveTemperature releve) {
    }
}
