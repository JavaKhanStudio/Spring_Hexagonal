package fr.meteo.maintenance.domaine.modele;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests de l'agregat Station du contexte MAINTENANCE.
 *
 * <p>Comparez-les avec {@code fr.meteo.surveillance.domaine.modele.StationMeteoTest} : meme
 * mot "station", regles totalement differentes. Aucune ligne de ce test ne parle
 * de temperature ni de canicule.</p>
 */
class StationTest {

    private static final LocalDate AUJOURD_HUI = LocalDate.of(2025, 7, 1);

    private final NumeroSerie numeroSerie = new NumeroSerie("MF-004217");

    @Test
    void une_station_nait_hors_service() {
        Station station = new Station(StationId.nouveau(), numeroSerie, AUJOURD_HUI.minusDays(10));

        assertThat(station.etat()).isEqualTo(EtatOperationnel.HORS_SERVICE);
    }

    @Test
    void se_met_en_service_si_l_etalonnage_est_valide() {
        Station station = new Station(StationId.nouveau(), numeroSerie, AUJOURD_HUI.minusDays(30));

        station.mettreEnService(AUJOURD_HUI);

        assertThat(station.etat()).isEqualTo(EtatOperationnel.EN_SERVICE);
    }

    @Test
    void refuse_la_mise_en_service_si_l_etalonnage_est_perime() {
        // 366 jours : au-dela de la validite reglementaire d'un an
        Station station = new Station(StationId.nouveau(), numeroSerie, AUJOURD_HUI.minusDays(366));

        assertThatThrownBy(() -> station.mettreEnService(AUJOURD_HUI))
                .isInstanceOf(Station.EtalonnagePerimeException.class);

        assertThat(station.etat()).isEqualTo(EtatOperationnel.HORS_SERVICE);
    }

    @Test
    void un_nouvel_etalonnage_reouvre_le_service() {
        Station station = new Station(StationId.nouveau(), numeroSerie, AUJOURD_HUI.minusDays(400));

        station.etalonner(AUJOURD_HUI, AUJOURD_HUI);

        assertThat(station.etalonnagePerime(AUJOURD_HUI)).isFalse();
        assertThat(station.etat()).isEqualTo(EtatOperationnel.EN_SERVICE);
    }

    @Test
    void refuse_un_etalonnage_date_du_futur() {
        Station station = new Station(StationId.nouveau(), numeroSerie, AUJOURD_HUI.minusDays(10));

        assertThatThrownBy(() -> station.etalonner(AUJOURD_HUI.plusDays(1), AUJOURD_HUI))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void refuse_un_numero_de_serie_hors_format() {
        assertThatThrownBy(() -> new NumeroSerie("1234"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void le_cycle_de_vie_passe_par_l_agregat() {
        Station station = new Station(StationId.nouveau(), numeroSerie, AUJOURD_HUI.minusDays(30));
        station.mettreEnService(AUJOURD_HUI);

        // aucun setter : l'etat ne change que par des methodes du metier
        station.demarrerEtalonnage();
        assertThat(station.etat()).isEqualTo(EtatOperationnel.EN_ETALONNAGE);

        station.mettreHorsService();
        assertThat(station.etat()).isEqualTo(EtatOperationnel.HORS_SERVICE);
    }
}
