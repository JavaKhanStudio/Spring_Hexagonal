package fr.meteo.domaine.modele;

import java.util.Objects;
import java.util.UUID;

/**
 * OBJET-VALEUR : identite d'une canicule.
 */
public record CaniculeId(UUID valeur) {

    public CaniculeId {
        Objects.requireNonNull(valeur, "L'identifiant de canicule est obligatoire");
    }

    public static CaniculeId nouveau() {
        return new CaniculeId(UUID.randomUUID());
    }

    public static CaniculeId de(String valeur) {
        return new CaniculeId(UUID.fromString(valeur));
    }

    @Override
    public String toString() {
        return valeur.toString();
    }
}
