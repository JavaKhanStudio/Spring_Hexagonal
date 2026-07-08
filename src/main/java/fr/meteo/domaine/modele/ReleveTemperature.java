package fr.meteo.domaine.modele;

import java.time.LocalDate;
import java.util.Objects;

/**
 * OBJET-VALEUR : un releve de temperature pour un jour donne.
 *
 * <p>Le modele choisit un releve <b>par jour</b> (et non a la minute), ce qui
 * colle a la definition metier d'une canicule : "plusieurs jours consecutifs
 * de forte chaleur". Le langage du code suit ainsi le langage du domaine.</p>
 */
public record ReleveTemperature(LocalDate jour, Temperature temperature) {

    public ReleveTemperature {
        Objects.requireNonNull(jour, "Le jour est obligatoire");
        Objects.requireNonNull(temperature, "La temperature est obligatoire");
    }
}
