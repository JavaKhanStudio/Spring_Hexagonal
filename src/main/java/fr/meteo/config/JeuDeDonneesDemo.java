package fr.meteo.config;

import fr.meteo.domaine.modele.Localisation;
import fr.meteo.domaine.modele.ReleveTemperature;
import fr.meteo.domaine.modele.StationId;
import fr.meteo.domaine.modele.Temperature;
import fr.meteo.domaine.port.entrant.CreerStation;
import fr.meteo.domaine.port.entrant.DetecterCanicules;
import fr.meteo.domaine.port.entrant.EnregistrerReleve;
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

    public JeuDeDonneesDemo(CreerStation creerStation,
                            EnregistrerReleve enregistrerReleve,
                            DetecterCanicules detecterCanicules) {
        this.creerStation = creerStation;
        this.enregistrerReleve = enregistrerReleve;
        this.detecterCanicules = detecterCanicules;
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
        log.info("Essayez : GET http://localhost:8080/api/canicules");
    }
}
