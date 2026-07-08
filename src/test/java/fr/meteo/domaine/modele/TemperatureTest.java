package fr.meteo.domaine.modele;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests de l'objet-valeur Temperature. Aucun framework : du JUnit pur, rapide,
 * qui documente les regles metier.
 */
class TemperatureTest {

    @Test
    void convertit_fahrenheit_en_celsius() {
        assertThat(Temperature.fahrenheit(77).valeurEnCelsius()).isEqualTo(25.0);
    }

    @Test
    void deux_temperatures_egales_meme_dans_des_unites_differentes() {
        assertThat(Temperature.celsius(25)).isEqualTo(Temperature.fahrenheit(77));
    }

    @Test
    void compare_la_chaleur_reelle_sans_se_soucier_de_l_unite() {
        // 100 F = 37,8 C, donc plus chaud que 30 C
        assertThat(Temperature.fahrenheit(100).estPlusChaudeQue(Temperature.celsius(30))).isTrue();
    }

    @Test
    void atteint_ou_depasse_un_seuil() {
        assertThat(Temperature.celsius(35).atteintOuDepasse(Temperature.celsius(35))).isTrue();
        assertThat(Temperature.celsius(34).atteintOuDepasse(Temperature.celsius(35))).isFalse();
    }

    @Test
    void refuse_une_temperature_sous_le_zero_absolu() {
        assertThatThrownBy(() -> Temperature.celsius(-300))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("zero absolu");
    }
}
