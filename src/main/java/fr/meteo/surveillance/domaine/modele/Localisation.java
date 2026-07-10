package fr.meteo.surveillance.domaine.modele;

import java.util.Objects;

/**
 * OBJET-VALEUR : l'endroit ou se trouve une station meteo.
 *
 * <p>Un {@code record} Java est parfait pour un objet-valeur simple : immuable,
 * egalite par valeur et {@code hashCode} generes automatiquement. On ajoute
 * seulement la validation dans le constructeur compact.</p>
 */
public record Localisation(String ville, String codePostal) {

    public Localisation {
        if (ville == null || ville.isBlank()) {
            throw new IllegalArgumentException("La ville est obligatoire");
        }
        if (codePostal == null || codePostal.isBlank()) {
            throw new IllegalArgumentException("Le code postal est obligatoire");
        }
    }

    @Override
    public String toString() {
        return ville + " (" + codePostal + ")";
    }
}
