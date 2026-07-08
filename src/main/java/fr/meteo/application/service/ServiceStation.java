package fr.meteo.application.service;

import fr.meteo.application.StationIntrouvableException;
import fr.meteo.domaine.modele.Localisation;
import fr.meteo.domaine.modele.StationId;
import fr.meteo.domaine.modele.StationMeteo;
import fr.meteo.domaine.port.entrant.CreerStation;
import fr.meteo.domaine.port.entrant.EnregistrerReleve;
import fr.meteo.domaine.port.sortant.DepotStationMeteo;

/**
 * SERVICE APPLICATIF (use case) : orchestre les operations sur les stations.
 *
 * <p>Un service applicatif est un chef d'orchestre mince : il charge un agregat
 * via un port sortant, lui demande d'appliquer une regle metier, puis le
 * sauvegarde. Il ne contient <b>aucune</b> logique metier lui-meme (celle-ci
 * vit dans les agregats et services de domaine) et <b>aucun</b> detail technique
 * (celui-ci vit dans les adaptateurs).</p>
 *
 * <p>Remarquez : pas d'annotation Spring ici. Cette classe est du Java pur,
 * testable avec de simples doublures. C'est une classe de configuration
 * ({@code fr.meteo.config.ConfigurationHexagonale}) qui l'instancie et
 * l'expose comme bean. Le coeur de l'application ignore le framework.</p>
 */
public class ServiceStation implements CreerStation, EnregistrerReleve {

    private final DepotStationMeteo depotStations;

    public ServiceStation(DepotStationMeteo depotStations) {
        this.depotStations = depotStations;
    }

    @Override
    public StationId creer(Localisation localisation) {
        StationMeteo station = new StationMeteo(StationId.nouveau(), localisation);
        depotStations.sauvegarder(station);
        return station.id();
    }

    @Override
    public StationId enregistrer(Commande commande) {
        StationMeteo station = depotStations.parId(commande.stationId())
                .orElseThrow(() -> new StationIntrouvableException(commande.stationId()));

        // La regle "un releve par jour" est appliquee par l'agregat lui-meme.
        station.enregistrer(commande.releve());

        depotStations.sauvegarder(station);
        return station.id();
    }
}
