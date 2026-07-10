package fr.meteo.surveillance.domaine.port.sortant;

import fr.meteo.surveillance.domaine.modele.StationId;
import fr.meteo.surveillance.domaine.modele.StationMeteo;

import java.util.Optional;

/**
 * PORT SORTANT (driven port) : le depot des stations meteo.
 *
 * <p>Un port sortant est une interface que <b>l'application appelle</b> pour
 * atteindre le monde exterieur (base de donnees, fichier, service tiers...).
 * Le domaine declare ici SES besoins ("charger", "sauvegarder une station")
 * en vocabulaire metier. C'est un adaptateur, dans la couche externe, qui
 * fournira l'implementation concrete (JPA, en memoire...).</p>
 *
 * <p>Consequence : le domaine ne connait ni JPA, ni SQL, ni H2. On pourrait
 * remplacer la base par un fichier texte sans toucher une ligne du domaine.</p>
 */
public interface DepotStationMeteo {

    void sauvegarder(StationMeteo station);

    Optional<StationMeteo> parId(StationId id);
}
