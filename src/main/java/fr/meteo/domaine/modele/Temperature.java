package fr.meteo.domaine.modele;

import java.util.Objects;

/**
 * OBJET-VALEUR (Value Object).
 *
 * <p>Une temperature est definie par sa valeur ET son unite. Deux temperatures
 * sont egales si elles representent la meme chaleur, meme exprimees dans des
 * unites differentes (25 C == 77 F).</p>
 *
 * <p>Proprietes d'un objet-valeur mises en oeuvre ici :</p>
 * <ul>
 *   <li><b>Immuable</b> : aucun setter, champs finaux. Toute "modification"
 *       renvoie une nouvelle instance (voir {@link #versCelsius()}).</li>
 *   <li><b>Sans identite</b> : on ne distingue pas deux 30 C, ils sont
 *       interchangeables. L'egalite se fait sur la valeur.</li>
 *   <li><b>Auto-validant</b> : impossible de creer une temperature sous le
 *       zero absolu.</li>
 *   <li><b>Porteur de comportement</b> : la conversion et la comparaison sont
 *       des regles du domaine, elles vivent donc ici et pas dans un "helper".</li>
 * </ul>
 */
public final class Temperature {

    /** Zero absolu, borne physique infranchissable, exprime en Celsius. */
    private static final double ZERO_ABSOLU_CELSIUS = -273.15;

    private final double valeur;
    private final UniteTemperature unite;

    private Temperature(double valeur, UniteTemperature unite) {
        this.unite = Objects.requireNonNull(unite, "L'unite est obligatoire");
        double enCelsius = convertirEnCelsius(valeur, unite);
        if (enCelsius < ZERO_ABSOLU_CELSIUS) {
            throw new IllegalArgumentException(
                    "Temperature impossible : " + valeur + unite.symbole()
                            + " est sous le zero absolu");
        }
        this.valeur = valeur;
    }

    /** Fabrique une temperature en degres Celsius. */
    public static Temperature celsius(double valeur) {
        return new Temperature(valeur, UniteTemperature.CELSIUS);
    }

    /** Fabrique une temperature en degres Fahrenheit. */
    public static Temperature fahrenheit(double valeur) {
        return new Temperature(valeur, UniteTemperature.FAHRENHEIT);
    }

    public static Temperature de(double valeur, UniteTemperature unite) {
        return new Temperature(valeur, unite);
    }

    public double valeur() {
        return valeur;
    }

    public UniteTemperature unite() {
        return unite;
    }

    /** Renvoie la meme temperature exprimee en Celsius (nouvel objet-valeur). */
    public Temperature versCelsius() {
        if (unite == UniteTemperature.CELSIUS) {
            return this;
        }
        return Temperature.celsius(convertirEnCelsius(valeur, unite));
    }

    /** Valeur numerique en Celsius, utile pour comparer sans se soucier de l'unite. */
    public double valeurEnCelsius() {
        return convertirEnCelsius(valeur, unite);
    }

    /** {@code true} si cette temperature est strictement plus chaude que l'autre. */
    public boolean estPlusChaudeQue(Temperature autre) {
        return this.valeurEnCelsius() > autre.valeurEnCelsius();
    }

    /** {@code true} si cette temperature atteint ou depasse le seuil donne. */
    public boolean atteintOuDepasse(Temperature seuil) {
        return this.valeurEnCelsius() >= seuil.valeurEnCelsius();
    }

    private static double convertirEnCelsius(double valeur, UniteTemperature unite) {
        return switch (unite) {
            case CELSIUS -> valeur;
            case FAHRENHEIT -> (valeur - 32) * 5.0 / 9.0;
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Temperature autre)) {
            return false;
        }
        // Egalite sur la chaleur reelle, a 0,0001 pres pour absorber les arrondis.
        return Math.abs(this.valeurEnCelsius() - autre.valeurEnCelsius()) < 1e-4;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Math.round(valeurEnCelsius() * 1000));
    }

    @Override
    public String toString() {
        return valeur + " " + unite.symbole();
    }
}
