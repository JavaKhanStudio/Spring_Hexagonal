package fr.meteo.domaine.modele;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/**
 * RACINE D'AGREGAT : une canicule detectee.
 *
 * <p>Represente une periode de chaleur soutenue sur une station. Points DDD
 * importants illustres ici :</p>
 * <ul>
 *   <li><b>Reference par identite</b> : la canicule ne contient pas l'objet
 *       {@link StationMeteo} mais son {@link StationId}. Chaque agregat est
 *       charge et sauvegarde independamment ; on ne traverse pas l'un pour
 *       modifier l'autre.</li>
 *   <li><b>Invariants a la construction</b> : une canicule a forcement une date
 *       de fin apres (ou egale a) sa date de debut, et couvre au moins un jour.
 *       Un objet du domaine est toujours dans un etat valide.</li>
 *   <li><b>Fabrique nommee</b> {@link #surPeriode} : le domaine calcule lui-meme
 *       la duree et le pic, on ne lui fournit pas des donnees incoherentes.</li>
 * </ul>
 */
public class Canicule {

    private final CaniculeId id;
    private final StationId stationId;
    private final Localisation localisation;
    private final LocalDate dateDebut;
    private final LocalDate dateFin;
    private final Temperature temperaturePic;

    private Canicule(CaniculeId id, StationId stationId, Localisation localisation,
                     LocalDate dateDebut, LocalDate dateFin, Temperature temperaturePic) {
        this.id = Objects.requireNonNull(id);
        this.stationId = Objects.requireNonNull(stationId);
        this.localisation = Objects.requireNonNull(localisation);
        this.dateDebut = Objects.requireNonNull(dateDebut);
        this.dateFin = Objects.requireNonNull(dateFin);
        this.temperaturePic = Objects.requireNonNull(temperaturePic);
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException(
                    "La date de fin (" + dateFin + ") precede la date de debut (" + dateDebut + ")");
        }
    }

    /**
     * Fabrique une canicule a partir de la serie de releves consecutifs qui la
     * composent. Le domaine en deduit le debut, la fin et la temperature de pic.
     *
     * @param releves releves consecutifs (au moins un), tous au-dela du seuil
     */
    public static Canicule surPeriode(StationId stationId, Localisation localisation,
                                      List<ReleveTemperature> releves) {
        if (releves == null || releves.isEmpty()) {
            throw new IllegalArgumentException("Une canicule couvre au moins un releve");
        }
        LocalDate debut = releves.getFirst().jour();
        LocalDate fin = releves.getLast().jour();
        Temperature pic = releves.stream()
                .map(ReleveTemperature::temperature)
                .reduce((a, b) -> a.estPlusChaudeQue(b) ? a : b)
                .orElseThrow();
        return new Canicule(CaniculeId.nouveau(), stationId, localisation, debut, fin, pic);
    }

    /** Reconstruction depuis la persistance (invariants revalides). */
    public static Canicule reconstituer(CaniculeId id, StationId stationId, Localisation localisation,
                                        LocalDate dateDebut, LocalDate dateFin, Temperature temperaturePic) {
        return new Canicule(id, stationId, localisation, dateDebut, dateFin, temperaturePic);
    }

    /** Nombre de jours couverts (bornes incluses). Comportement, pas donnee stockee. */
    public long nombreDeJours() {
        return ChronoUnit.DAYS.between(dateDebut, dateFin) + 1;
    }

    public CaniculeId id() {
        return id;
    }

    public StationId stationId() {
        return stationId;
    }

    public Localisation localisation() {
        return localisation;
    }

    public LocalDate dateDebut() {
        return dateDebut;
    }

    public LocalDate dateFin() {
        return dateFin;
    }

    public Temperature temperaturePic() {
        return temperaturePic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Canicule autre)) {
            return false;
        }
        return id.equals(autre.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
