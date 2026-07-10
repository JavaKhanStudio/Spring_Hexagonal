package fr.meteo.maintenance.domaine.port.sortant;

import fr.meteo.maintenance.domaine.modele.Station;
import fr.meteo.maintenance.domaine.modele.StationId;

import java.util.Optional;

/**
 * PORT SORTANT du contexte maintenance.
 *
 * <p>Chaque contexte delimite a son propre hexagone, donc ses propres ports.
 * Ce depot manipule la {@link Station} de la maintenance, pas la
 * {@code StationMeteo} de la surveillance : deux contextes, deux depots, meme
 * si en base les deux tables designent le meme materiel.</p>
 */
public interface DepotStation {

    void sauvegarder(Station station);

    Optional<Station> parId(StationId id);
}
