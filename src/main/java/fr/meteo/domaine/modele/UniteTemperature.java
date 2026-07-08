package fr.meteo.domaine.modele;

/**
 * Unite de mesure d'une temperature.
 *
 * <p>Fait partie du "langage omnipresent" (ubiquitous language) du domaine :
 * on parle de degres Celsius ou Fahrenheit, jamais de "type = 0 ou 1".</p>
 */
public enum UniteTemperature {

    CELSIUS("C"),
    FAHRENHEIT("F");

    private final String symbole;

    UniteTemperature(String symbole) {
        this.symbole = symbole;
    }

    public String symbole() {
        return symbole;
    }
}
