package fr.meteo.maintenance.domaine.port.entrant;

import fr.meteo.maintenance.domaine.modele.Station;
import fr.meteo.maintenance.domaine.modele.StationId;

import java.util.Optional;

/**
 * PORT ENTRANT du contexte maintenance : "consulter l'etat d'une station".
 *
 * <p>Le contexte maintenance possede son propre hexagone, donc ses propres
 * ports entrants. Aucun rapport avec {@code ConsulterCanicules} : les deux
 * contextes exposent des cas d'usage differents sur le meme materiel.</p>
 */
public interface ConsulterStation {

    Optional<Station> parId(StationId id);
}
