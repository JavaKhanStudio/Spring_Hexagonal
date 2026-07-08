package fr.meteo.domaine.modele;

import java.util.Objects;

/**
 * OBJET-VALEUR faisant office de POLITIQUE metier (policy).
 *
 * <p>Definit ce qui declenche une canicule : une temperature doit atteindre un
 * seuil pendant un nombre minimum de jours consecutifs. Rendre cette regle
 * explicite (plutot que des nombres "magiques" caches dans le code) est un
 * principe fort du DDD : la connaissance metier devient un objet nomme.</p>
 */
public record SeuilCanicule(Temperature temperatureSeuil, int joursConsecutifsMin) {

    public SeuilCanicule {
        Objects.requireNonNull(temperatureSeuil, "La temperature seuil est obligatoire");
        if (joursConsecutifsMin < 1) {
            throw new IllegalArgumentException(
                    "Il faut au moins 1 jour consecutif, recu : " + joursConsecutifsMin);
        }
    }

    /**
     * Seuil par defaut inspire des criteres francais : une vague de chaleur
     * marquee correspond a environ 3 jours consecutifs au-dela de 35 C.
     */
    public static SeuilCanicule parDefaut() {
        return new SeuilCanicule(Temperature.celsius(35), 3);
    }

    /** {@code true} si un releve franchit le seuil de temperature. */
    public boolean estFranchiPar(ReleveTemperature releve) {
        return releve.temperature().atteintOuDepasse(temperatureSeuil);
    }
}
