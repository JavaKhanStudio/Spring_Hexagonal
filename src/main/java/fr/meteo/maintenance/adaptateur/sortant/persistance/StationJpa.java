package fr.meteo.maintenance.adaptateur.sortant.persistance;

import fr.meteo.maintenance.domaine.modele.EtatOperationnel;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

/**
 * ENTITE DE PERSISTANCE du contexte MAINTENANCE.
 *
 * <p>A comparer avec {@code StationMeteoJpa} de l'autre contexte. Les deux
 * decrivent le meme boitier physique, mais <b>aucune colonne n'est commune</b>
 * en dehors de l'identifiant :</p>
 *
 * <table>
 *   <caption>Deux tables pour un meme equipement</caption>
 *   <tr><th>{@code stations} (surveillance)</th><th>{@code stations_maintenance}</th></tr>
 *   <tr><td>id, ville, code_postal, releves[]</td><td>id, numero_serie, etat, dernier_etalonnage</td></tr>
 * </table>
 *
 * <p>Les deux tables se correlent par la <b>valeur brute</b> de l'identifiant,
 * jamais par une cle etrangere entre modeles. C'est la traduction, au niveau du
 * schema, de la frontiere de contexte delimite.</p>
 */
@Entity
@Table(name = "stations_maintenance")
public class StationJpa {

    @Id
    private UUID id;

    private String numeroSerie;

    @Enumerated(EnumType.STRING)
    private EtatOperationnel etat;

    private LocalDate dernierEtalonnage;

    protected StationJpa() {
        // requis par JPA
    }

    public StationJpa(UUID id, String numeroSerie, EtatOperationnel etat, LocalDate dernierEtalonnage) {
        this.id = id;
        this.numeroSerie = numeroSerie;
        this.etat = etat;
        this.dernierEtalonnage = dernierEtalonnage;
    }

    public UUID getId() {
        return id;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public EtatOperationnel getEtat() {
        return etat;
    }

    public LocalDate getDernierEtalonnage() {
        return dernierEtalonnage;
    }
}
