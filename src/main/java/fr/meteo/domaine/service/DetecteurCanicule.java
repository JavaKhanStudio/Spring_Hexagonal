package fr.meteo.domaine.service;

import fr.meteo.domaine.modele.Canicule;
import fr.meteo.domaine.modele.ReleveTemperature;
import fr.meteo.domaine.modele.SeuilCanicule;
import fr.meteo.domaine.modele.StationMeteo;

import java.util.ArrayList;
import java.util.List;

/**
 * SERVICE DE DOMAINE (Domain Service).
 *
 * <p>Certaines regles metier ne se rattachent naturellement a aucun agregat :
 * detecter les canicules exige de raisonner sur une <b>serie</b> de releves et
 * d'en produire de <b>nouveaux</b> agregats {@link Canicule}. On la place donc
 * dans un service de domaine, objet sans etat, au vocabulaire purement metier.</p>
 *
 * <p>Ce n'est PAS un {@code @Service} Spring : c'est du Java pur, sans aucune
 * dependance a un framework. On pourrait l'executer dans un simple {@code main}.
 * C'est la promesse de l'architecture hexagonale : le coeur metier est isole.</p>
 *
 * <p>Regle appliquee : on cherche les plus longues suites de jours
 * <b>calendairement consecutifs</b> dont la temperature franchit le seuil ;
 * une suite d'au moins {@code joursConsecutifsMin} jours devient une canicule.</p>
 */
public class DetecteurCanicule {

    public List<Canicule> detecter(StationMeteo station, SeuilCanicule seuil) {
        List<Canicule> canicules = new ArrayList<>();
        List<ReleveTemperature> serieEnCours = new ArrayList<>();

        for (ReleveTemperature releve : station.releves()) {
            if (seuil.estFranchiPar(releve) && seContinue(serieEnCours, releve)) {
                serieEnCours.add(releve);
            } else {
                cloturer(serieEnCours, seuil, station, canicules);
                serieEnCours = new ArrayList<>();
                if (seuil.estFranchiPar(releve)) {
                    serieEnCours.add(releve);
                }
            }
        }
        cloturer(serieEnCours, seuil, station, canicules);
        return canicules;
    }

    /** Un releve prolonge la serie s'il tombe le lendemain du dernier releve. */
    private boolean seContinue(List<ReleveTemperature> serie, ReleveTemperature releve) {
        if (serie.isEmpty()) {
            return true;
        }
        ReleveTemperature dernier = serie.getLast();
        return releve.jour().equals(dernier.jour().plusDays(1));
    }

    /** Transforme la serie courante en canicule si elle est assez longue. */
    private void cloturer(List<ReleveTemperature> serie, SeuilCanicule seuil,
                          StationMeteo station, List<Canicule> canicules) {
        if (serie.size() >= seuil.joursConsecutifsMin()) {
            canicules.add(Canicule.surPeriode(station.id(), station.localisation(), serie));
        }
    }
}
