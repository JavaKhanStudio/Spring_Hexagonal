package fr.meteo.adaptateur.sortant.persistance;

import fr.meteo.domaine.modele.UniteTemperature;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;

/**
 * ENTITE DE PERSISTANCE d'un releve. Notez qu'un objet-valeur riche du domaine
 * ({@code Temperature}) est ici "aplati" en deux colonnes simples
 * (valeur + unite) : le mapping traduit entre les deux mondes.
 */
@Entity
@Table(name = "releves")
public class ReleveTemperatureJpa {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate jour;

    private double valeur;

    @Enumerated(EnumType.STRING)
    private UniteTemperature unite;

    protected ReleveTemperatureJpa() {
        // requis par JPA
    }

    public ReleveTemperatureJpa(LocalDate jour, double valeur, UniteTemperature unite) {
        this.jour = jour;
        this.valeur = valeur;
        this.unite = unite;
    }

    public LocalDate getJour() {
        return jour;
    }

    public double getValeur() {
        return valeur;
    }

    public UniteTemperature getUnite() {
        return unite;
    }
}
