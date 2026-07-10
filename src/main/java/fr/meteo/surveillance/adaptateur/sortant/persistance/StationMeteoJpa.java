package fr.meteo.surveillance.adaptateur.sortant.persistance;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * ENTITE DE PERSISTANCE (modele de donnees), volontairement SEPAREE du modele
 * de domaine {@code StationMeteo}.
 *
 * <p>Pourquoi ne pas mettre les annotations {@code @Entity} directement sur
 * l'agregat du domaine ? Parce que cela ferait fuiter la technique (JPA,
 * getters/setters, constructeur vide, mapping des colonnes) dans le coeur
 * metier, et forcerait le domaine a se plier aux contraintes de l'ORM plutot
 * qu'aux besoins metier. On garde donc deux modeles et un {@code Mapper} entre
 * eux : le domaine reste pur, la persistance reste libre d'evoluer.</p>
 *
 * <p>Contrainte technique typique visible ici : JPA exige un constructeur sans
 * argument et des champs mutables — exactement ce qu'on NE veut PAS dans un
 * objet-valeur du domaine.</p>
 */
@Entity
@Table(name = "stations")
public class StationMeteoJpa {

    @Id
    private UUID id;

    private String ville;
    private String codePostal;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReleveTemperatureJpa> releves = new ArrayList<>();

    protected StationMeteoJpa() {
        // requis par JPA
    }

    public StationMeteoJpa(UUID id, String ville, String codePostal, List<ReleveTemperatureJpa> releves) {
        this.id = id;
        this.ville = ville;
        this.codePostal = codePostal;
        this.releves = releves;
    }

    public UUID getId() {
        return id;
    }

    public String getVille() {
        return ville;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public List<ReleveTemperatureJpa> getReleves() {
        return releves;
    }
}
