package fr.meteo.config;

import fr.meteo.surveillance.domaine.modele.Localisation;
import fr.meteo.surveillance.domaine.modele.ReleveTemperature;
import fr.meteo.surveillance.domaine.modele.StationId;
import fr.meteo.surveillance.domaine.modele.Temperature;
import fr.meteo.surveillance.domaine.port.entrant.CreerStation;
import fr.meteo.surveillance.domaine.port.entrant.DetecterCanicules;
import fr.meteo.surveillance.domaine.port.entrant.EnregistrerReleve;
import fr.meteo.maintenance.domaine.modele.NumeroSerie;
import fr.meteo.maintenance.domaine.modele.Station;
import fr.meteo.maintenance.domaine.port.sortant.DepotStation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Amorce un jeu de donnees au demarrage pour que la demo soit vivante.
 *
 * <p>Interessant pedagogiquement : ce composant technique pilote l'application
 * exactement comme le ferait un client REST, en passant par les PORTS ENTRANTS.
 * Il n'accede jamais directement a la base : il "consomme" l'application par sa
 * facade metier, preuve que les ports suffisent.</p>
 */
@Component
public class JeuDeDonneesDemo implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(JeuDeDonneesDemo.class);

    private final CreerStation creerStation;
    private final EnregistrerReleve enregistrerReleve;
    private final DetecterCanicules detecterCanicules;
    private final DepotStation depotMaintenance;

    public JeuDeDonneesDemo(CreerStation creerStation,
                            EnregistrerReleve enregistrerReleve,
                            DetecterCanicules detecterCanicules,
                            DepotStation depotMaintenance) {
        this.creerStation = creerStation;
        this.enregistrerReleve = enregistrerReleve;
        this.detecterCanicules = detecterCanicules;
        this.depotMaintenance = depotMaintenance;
    }

    @Override
    public void run(String... args) {
        StationId lyon = creerStation.creer(new Localisation("Lyon", "69000"));

        // Une vague de chaleur nette : 4 jours consecutifs au-dela de 35 C,
        // encadree par des journees plus douces.
        double[] temperatures = {28, 31, 36, 38, 40, 37, 30, 27};
        LocalDate depart = LocalDate.of(2025, 7, 10);
        for (int jour = 0; jour < temperatures.length; jour++) {
            ReleveTemperature releve = new ReleveTemperature(
                    depart.plusDays(jour),
                    Temperature.celsius(temperatures[jour]));
            enregistrerReleve.enregistrer(new EnregistrerReleve.Commande(lyon, releve));
        }

        int nb = detecterCanicules.detecterPour(lyon).size();
        log.info("Jeu de donnees demo pret. Station Lyon {} -> {} canicule(s) detectee(s).", lyon, nb);

        // Le MEME boitier physique, vu par le contexte maintenance.
        // Les deux contextes ne partagent que la valeur brute de l'identifiant :
        // on traverse la frontiere par un UUID, jamais par une classe.
        depotMaintenance.sauvegarder(new Station(
                new fr.meteo.maintenance.domaine.modele.StationId(lyon.valeur()),
                new NumeroSerie("MF-004217"),
                LocalDate.of(2025, 6, 1)));

        log.info("Essayez : GET http://localhost:8080/api/canicules");
        log.info("Le meme boitier, deux vues : GET /api/stations/{} ", lyon);
        log.info("                             GET /api/maintenance/stations/{}", lyon);
    }
}
