package fr.meteo.domaine.modele;

import java.util.Objects;
import java.util.UUID;

/**
 * OBJET-VALEUR : identite d'une station meteo.
 *
 * <p>Un identifiant type (plutot qu'un {@code String} ou {@code UUID} nu) evite
 * les confusions : impossible de passer par erreur un identifiant de canicule la
 * ou on attend une station. Le compilateur devient un allie du domaine.</p>
 */
public record StationId(UUID valeur) {

    public StationId {
        Objects.requireNonNull(valeur, "L'identifiant de station est obligatoire");
    }

    public static StationId nouveau() {
        return new StationId(UUID.randomUUID());
    }

    public static StationId de(String valeur) {
        return new StationId(UUID.fromString(valeur));
    }

    @Override
    public String toString() {
        return valeur.toString();
    }
}
