package fr.meteo.config;

import fr.meteo.application.service.ServiceDetectionCanicule;
import fr.meteo.application.service.ServiceStation;
import fr.meteo.domaine.modele.SeuilCanicule;
import fr.meteo.domaine.port.sortant.DepotCanicule;
import fr.meteo.domaine.port.sortant.DepotStationMeteo;
import fr.meteo.domaine.port.sortant.NotificateurCanicule;
import fr.meteo.domaine.service.DetecteurCanicule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RACINE DE COMPOSITION (Composition Root).
 *
 * <p>C'est ICI, et seulement ici, que Spring rencontre le coeur metier. Cette
 * classe d'infrastructure instancie les objets purs du domaine et de
 * l'application, et les expose comme beans en leur injectant les adaptateurs
 * sortants (qui, eux, sont des {@code @Component} decouverts automatiquement).</p>
 *
 * <p>Grace a ce cablage explicite, les couches {@code domaine} et
 * {@code application} n'ont AUCUNE annotation Spring : elles restent du Java pur,
 * testable sans demarrer de contexte. Un test d'architecture (ArchUnit) verifie
 * cet interdit et casse la compilation s'il est viole.</p>
 */
@Configuration
public class ConfigurationHexagonale {

    /** Le seuil metier declenchant une canicule (temperature + duree). */
    @Bean
    public SeuilCanicule seuilCanicule() {
        return SeuilCanicule.parDefaut();
    }

    /** Le service de domaine, sans etat, qui applique la regle de detection. */
    @Bean
    public DetecteurCanicule detecteurCanicule() {
        return new DetecteurCanicule();
    }

    /**
     * Service applicatif des stations. Il implemente les ports entrants
     * CreerStation et EnregistrerReleve : les controleurs pourront injecter
     * l'un ou l'autre, Spring resoudra ce bean.
     */
    @Bean
    public ServiceStation serviceStation(DepotStationMeteo depotStations) {
        return new ServiceStation(depotStations);
    }

    /** Service applicatif de detection (ports DetecterCanicules + ConsulterCanicules). */
    @Bean
    public ServiceDetectionCanicule serviceDetectionCanicule(DepotStationMeteo depotStations,
                                                             DepotCanicule depotCanicules,
                                                             NotificateurCanicule notificateur,
                                                             DetecteurCanicule detecteur,
                                                             SeuilCanicule seuil) {
        return new ServiceDetectionCanicule(depotStations, depotCanicules, notificateur, detecteur, seuil);
    }
}
