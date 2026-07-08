package fr.meteo.application.service;

import fr.meteo.application.StationIntrouvableException;
import fr.meteo.domaine.modele.Canicule;
import fr.meteo.domaine.modele.SeuilCanicule;
import fr.meteo.domaine.modele.StationId;
import fr.meteo.domaine.modele.StationMeteo;
import fr.meteo.domaine.port.entrant.ConsulterCanicules;
import fr.meteo.domaine.port.entrant.DetecterCanicules;
import fr.meteo.domaine.port.sortant.DepotCanicule;
import fr.meteo.domaine.port.sortant.DepotStationMeteo;
import fr.meteo.domaine.port.sortant.NotificateurCanicule;
import fr.meteo.domaine.service.DetecteurCanicule;

import java.util.List;

/**
 * SERVICE APPLICATIF : detection et consultation des canicules.
 *
 * <p>Illustre l'orchestration typique d'un cas d'usage hexagonal :</p>
 * <ol>
 *   <li>charger un agregat via un port sortant ({@link DepotStationMeteo}) ;</li>
 *   <li>deleguer la regle metier a un service de domaine
 *       ({@link DetecteurCanicule}) ;</li>
 *   <li>persister le resultat ({@link DepotCanicule}) et declencher un effet de
 *       bord ({@link NotificateurCanicule}) via d'autres ports sortants.</li>
 * </ol>
 *
 * <p>Le service applicatif "cable" les ports entre eux mais ne connait que des
 * interfaces : il ignore quelle base ou quel canal d'alerte se cache derriere.</p>
 */
public class ServiceDetectionCanicule implements DetecterCanicules, ConsulterCanicules {

    private final DepotStationMeteo depotStations;
    private final DepotCanicule depotCanicules;
    private final NotificateurCanicule notificateur;
    private final DetecteurCanicule detecteur;
    private final SeuilCanicule seuil;

    public ServiceDetectionCanicule(DepotStationMeteo depotStations,
                                    DepotCanicule depotCanicules,
                                    NotificateurCanicule notificateur,
                                    DetecteurCanicule detecteur,
                                    SeuilCanicule seuil) {
        this.depotStations = depotStations;
        this.depotCanicules = depotCanicules;
        this.notificateur = notificateur;
        this.detecteur = detecteur;
        this.seuil = seuil;
    }

    @Override
    public List<Canicule> detecterPour(StationId stationId) {
        StationMeteo station = depotStations.parId(stationId)
                .orElseThrow(() -> new StationIntrouvableException(stationId));

        List<Canicule> deja = depotCanicules.parStation(stationId);

        List<Canicule> nouvelles = detecteur.detecter(station, seuil).stream()
                .filter(candidate -> estInedite(candidate, deja))
                .toList();

        nouvelles.forEach(canicule -> {
            depotCanicules.sauvegarder(canicule);
            notificateur.alerter(canicule);
        });

        return nouvelles;
    }

    @Override
    public List<Canicule> toutes() {
        return depotCanicules.toutes();
    }

    /** Une canicule est inedite si aucune connue ne couvre la meme periode. */
    private boolean estInedite(Canicule candidate, List<Canicule> connues) {
        return connues.stream().noneMatch(connue ->
                connue.dateDebut().equals(candidate.dateDebut())
                        && connue.dateFin().equals(candidate.dateFin()));
    }
}
