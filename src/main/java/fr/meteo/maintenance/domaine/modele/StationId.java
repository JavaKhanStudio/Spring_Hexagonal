package fr.meteo.maintenance.domaine.modele;

import java.util.Objects;
import java.util.UUID;

/**
 * OBJET-VALEUR : identite d'une station, <b>vue par le contexte maintenance</b>.
 *
 * <p>Oui, {@code fr.meteo.surveillance.domaine.modele.StationId} existe deja et lui ressemble
 * comme un jumeau. Cette duplication est <b>deliberee</b> : c'est la regle des
 * contextes delimites.</p>
 *
 * <p>Si la maintenance importait la classe du contexte surveillance, les deux
 * modeles seraient soudes : ajouter un champ ou changer le type de
 * l'identifiant cote surveillance casserait la maintenance. Les contextes ne
 * partagent que la <b>valeur brute</b> ({@link UUID}), qui joue le role
 * d'identifiant de correlation entre eux.</p>
 *
 * <p>Retenir : dans un contexte delimite, la duplication d'un concept est un
 * choix de conception, pas un manque de factorisation. On factorise a
 * l'interieur d'un contexte, jamais entre deux.</p>
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
