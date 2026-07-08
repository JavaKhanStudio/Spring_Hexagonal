package fr.meteo.domaine.service;

import fr.meteo.domaine.modele.Canicule;
import fr.meteo.domaine.modele.Localisation;
import fr.meteo.domaine.modele.ReleveTemperature;
import fr.meteo.domaine.modele.SeuilCanicule;
import fr.meteo.domaine.modele.StationId;
import fr.meteo.domaine.modele.StationMeteo;
import fr.meteo.domaine.modele.Temperature;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests du service de domaine DetecteurCanicule : le coeur de la regle metier.
 */
class DetecteurCaniculeTest {

    private final DetecteurCanicule detecteur = new DetecteurCanicule();
    private final SeuilCanicule seuil = new SeuilCanicule(Temperature.celsius(35), 3);

    @Test
    void detecte_une_canicule_de_trois_jours_consecutifs() {
        StationMeteo station = stationAvec(
                releve("2025-07-01", 36),
                releve("2025-07-02", 37),
                releve("2025-07-03", 38));

        var canicules = detecteur.detecter(station, seuil);

        assertThat(canicules).hasSize(1);
        Canicule c = canicules.getFirst();
        assertThat(c.dateDebut()).isEqualTo(LocalDate.parse("2025-07-01"));
        assertThat(c.dateFin()).isEqualTo(LocalDate.parse("2025-07-03"));
        assertThat(c.nombreDeJours()).isEqualTo(3);
        assertThat(c.temperaturePic()).isEqualTo(Temperature.celsius(38));
    }

    @Test
    void ne_detecte_rien_si_la_serie_est_trop_courte() {
        StationMeteo station = stationAvec(
                releve("2025-07-01", 36),
                releve("2025-07-02", 37));

        assertThat(detecteur.detecter(station, seuil)).isEmpty();
    }

    @Test
    void un_jour_sous_le_seuil_coupe_la_serie() {
        StationMeteo station = stationAvec(
                releve("2025-07-01", 36),
                releve("2025-07-02", 30),  // trou : casse la suite
                releve("2025-07-03", 37),
                releve("2025-07-04", 38));

        // Seule la seconde suite (3 jours ? non, 2 jours) -> aucune canicule
        assertThat(detecteur.detecter(station, seuil)).isEmpty();
    }

    @Test
    void un_jour_calendaire_manquant_coupe_la_serie() {
        StationMeteo station = stationAvec(
                releve("2025-07-01", 36),
                releve("2025-07-02", 37),
                // pas de releve le 03
                releve("2025-07-04", 38),
                releve("2025-07-05", 39),
                releve("2025-07-06", 40));

        var canicules = detecteur.detecter(station, seuil);

        assertThat(canicules).hasSize(1);
        assertThat(canicules.getFirst().dateDebut()).isEqualTo(LocalDate.parse("2025-07-04"));
        assertThat(canicules.getFirst().nombreDeJours()).isEqualTo(3);
    }

    @Test
    void detecte_deux_canicules_distinctes() {
        StationMeteo station = stationAvec(
                releve("2025-07-01", 36),
                releve("2025-07-02", 37),
                releve("2025-07-03", 38),
                releve("2025-07-04", 20),  // repos
                releve("2025-07-05", 36),
                releve("2025-07-06", 37),
                releve("2025-07-07", 41));

        var canicules = detecteur.detecter(station, seuil);

        assertThat(canicules).hasSize(2);
        assertThat(canicules.get(1).temperaturePic()).isEqualTo(Temperature.celsius(41));
    }

    private StationMeteo stationAvec(ReleveTemperature... releves) {
        StationMeteo station = new StationMeteo(StationId.nouveau(), new Localisation("Lyon", "69000"));
        for (ReleveTemperature r : releves) {
            station.enregistrer(r);
        }
        return station;
    }

    private ReleveTemperature releve(String jour, double celsius) {
        return new ReleveTemperature(LocalDate.parse(jour), Temperature.celsius(celsius));
    }
}
