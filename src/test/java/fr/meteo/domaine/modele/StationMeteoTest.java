package fr.meteo.domaine.modele;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests de l'agregat StationMeteo, gardien de l'invariant "un releve par jour".
 */
class StationMeteoTest {

    private final Localisation lyon = new Localisation("Lyon", "69000");

    @Test
    void enregistre_un_releve() {
        StationMeteo station = new StationMeteo(StationId.nouveau(), lyon);

        station.enregistrer(new ReleveTemperature(LocalDate.of(2025, 7, 1), Temperature.celsius(36)));

        assertThat(station.releves()).hasSize(1);
    }

    @Test
    void refuse_deux_releves_le_meme_jour() {
        StationMeteo station = new StationMeteo(StationId.nouveau(), lyon);
        LocalDate jour = LocalDate.of(2025, 7, 1);
        station.enregistrer(new ReleveTemperature(jour, Temperature.celsius(36)));

        assertThatThrownBy(() ->
                station.enregistrer(new ReleveTemperature(jour, Temperature.celsius(37))))
                .isInstanceOf(StationMeteo.ReleveDejaPresentException.class);
    }

    @Test
    void expose_les_releves_tries_par_date() {
        StationMeteo station = new StationMeteo(StationId.nouveau(), lyon);
        station.enregistrer(new ReleveTemperature(LocalDate.of(2025, 7, 3), Temperature.celsius(38)));
        station.enregistrer(new ReleveTemperature(LocalDate.of(2025, 7, 1), Temperature.celsius(36)));

        assertThat(station.releves())
                .extracting(ReleveTemperature::jour)
                .containsExactly(LocalDate.of(2025, 7, 1), LocalDate.of(2025, 7, 3));
    }

    @Test
    void la_liste_exposee_est_non_modifiable() {
        StationMeteo station = new StationMeteo(StationId.nouveau(), lyon);

        assertThatThrownBy(() -> station.releves()
                .add(new ReleveTemperature(LocalDate.of(2025, 7, 1), Temperature.celsius(36))))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
