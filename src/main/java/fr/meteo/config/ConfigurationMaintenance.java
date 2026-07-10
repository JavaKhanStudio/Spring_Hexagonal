package fr.meteo.config;

import fr.meteo.maintenance.application.service.ServiceMaintenance;
import fr.meteo.maintenance.domaine.port.sortant.DepotStation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RACINE DE COMPOSITION du contexte maintenance.
 *
 * <p>Voisine de {@link ConfigurationSurveillance}, et symetrique : chaque
 * contexte est cable dans sa propre classe. La racine de composition est le seul
 * endroit du systeme qui a le droit de connaitre les deux contextes a la fois --
 * un test d'architecture verifie que la reciproque est fausse.</p>
 */
@Configuration
public class ConfigurationMaintenance {

    /** Service applicatif en Java pur : Spring lui injecte l'adaptateur sortant. */
    @Bean
    public ServiceMaintenance serviceMaintenance(DepotStation depotStations) {
        return new ServiceMaintenance(depotStations);
    }
}
