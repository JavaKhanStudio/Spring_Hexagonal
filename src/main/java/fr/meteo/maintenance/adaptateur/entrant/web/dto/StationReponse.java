package fr.meteo.maintenance.adaptateur.entrant.web.dto;

import fr.meteo.maintenance.domaine.modele.Station;

import java.time.LocalDate;

/**
 * DTO de sortie du contexte MAINTENANCE.
 *
 * <p>Cette classe porte <b>exactement le meme nom</b> que
 * {@code fr.meteo.surveillance.adaptateur.entrant.web.dto.StationReponse}, et
 * n'expose pas un seul champ en commun avec elle :</p>
 *
 * <pre>
 * surveillance : { id, ville, codePostal }
 * maintenance  : { id, numeroSerie, etat, dernierEtalonnage, etalonnagePerime }
 * </pre>
 *
 * <p>Deux contextes peuvent nommer leurs types de la meme facon : le paquet fait
 * la difference, comme le fait le contexte a l'oral. C'est le langage
 * omnipresent qui s'applique, chacun dans sa frontiere.</p>
 *
 * <p>Notez que {@code aujourdHui} est <b>fourni par l'adaptateur</b>. Le domaine
 * n'appelle jamais {@code LocalDate.now()} : le temps est une entree, pas une
 * dependance cachee. C'est ce qui rend {@code Station} testable sans horloge.</p>
 */
public record StationReponse(
        String id,
        String numeroSerie,
        String etat,
        LocalDate dernierEtalonnage,
        boolean etalonnagePerime) {

    public static StationReponse depuis(Station station, LocalDate aujourdHui) {
        return new StationReponse(
                station.id().toString(),
                station.numeroSerie().valeur(),
                station.etat().name(),
                station.dernierEtalonnage(),
                station.etalonnagePerime(aujourdHui));
    }
}
