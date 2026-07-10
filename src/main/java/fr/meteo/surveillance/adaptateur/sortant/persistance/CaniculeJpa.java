package fr.meteo.surveillance.adaptateur.sortant.persistance;

import fr.meteo.surveillance.domaine.modele.UniteTemperature;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

/**
 * ENTITE DE PERSISTANCE d'une canicule.
 *
 * <p>La reference vers la station est un simple {@code UUID} (pas une relation
 * JPA vers {@code StationMeteoJpa}) : cela reflete la regle DDD "un agregat en
 * reference un autre par identite". Chaque agregat est un ilot de persistance.</p>
 */
@Entity
@Table(name = "canicules")
public class CaniculeJpa {

    @Id
    private UUID id;

    private UUID stationId;
    private String ville;
    private String codePostal;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private double picValeur;

    @Enumerated(EnumType.STRING)
    private UniteTemperature picUnite;

    protected CaniculeJpa() {
        // requis par JPA
    }

    public CaniculeJpa(UUID id, UUID stationId, String ville, String codePostal,
                       LocalDate dateDebut, LocalDate dateFin, double picValeur, UniteTemperature picUnite) {
        this.id = id;
        this.stationId = stationId;
        this.ville = ville;
        this.codePostal = codePostal;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.picValeur = picValeur;
        this.picUnite = picUnite;
    }

    public UUID getId() {
        return id;
    }

    public UUID getStationId() {
        return stationId;
    }

    public String getVille() {
        return ville;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public double getPicValeur() {
        return picValeur;
    }

    public UniteTemperature getPicUnite() {
        return picUnite;
    }
}
