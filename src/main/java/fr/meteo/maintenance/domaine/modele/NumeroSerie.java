package fr.meteo.maintenance.domaine.modele;

import java.util.regex.Pattern;

/**
 * OBJET-VALEUR : le numero de serie grave sur le boitier d'une station.
 *
 * <p>Ce concept n'existe <b>que</b> dans le contexte maintenance. Le contexte
 * surveillance, lui, identifie une station par sa {@code Localisation} : savoir
 * quel boitier est installe a Lyon ne l'interesse pas.</p>
 *
 * <p>C'est le signe d'une frontiere de contexte bien placee : chaque cote
 * possede du vocabulaire que l'autre n'a aucune raison de connaitre.</p>
 */
public record NumeroSerie(String valeur) {

    /** Format impose par le constructeur du materiel : MF-123456. */
    private static final Pattern FORMAT = Pattern.compile("MF-\\d{6}");

    public NumeroSerie {
        if (valeur == null || !FORMAT.matcher(valeur).matches()) {
            throw new IllegalArgumentException(
                    "Numero de serie invalide : " + valeur + " (attendu MF-123456)");
        }
    }

    @Override
    public String toString() {
        return valeur;
    }
}
