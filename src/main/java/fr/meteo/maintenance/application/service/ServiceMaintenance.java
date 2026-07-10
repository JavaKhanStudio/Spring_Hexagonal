package fr.meteo.maintenance.application.service;

import fr.meteo.maintenance.domaine.modele.Station;
import fr.meteo.maintenance.domaine.modele.StationId;
import fr.meteo.maintenance.domaine.port.entrant.ConsulterStation;
import fr.meteo.maintenance.domaine.port.sortant.DepotStation;

import java.util.Optional;

/**
 * SERVICE APPLICATIF du contexte maintenance.
 *
 * <p>Meme forme que {@code ServiceDetectionCanicule} en face : du Java pur, sans
 * annotation Spring, qui implemente un port entrant et delegue a un port sortant.
 * C'est la racine de composition qui l'instancie.</p>
 */
public class ServiceMaintenance implements ConsulterStation {

    private final DepotStation depotStations;

    public ServiceMaintenance(DepotStation depotStations) {
        this.depotStations = depotStations;
    }

    @Override
    public Optional<Station> parId(StationId id) {
        return depotStations.parId(id);
    }
}
