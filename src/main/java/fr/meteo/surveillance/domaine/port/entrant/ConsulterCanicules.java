package fr.meteo.surveillance.domaine.port.entrant;

import fr.meteo.surveillance.domaine.modele.Canicule;

import java.util.List;

/**
 * PORT ENTRANT : cas d'usage de lecture "consulter les canicules connues".
 *
 * <p>Separer la lecture de l'ecriture (ici de {@link DetecterCanicules}) garde
 * des cas d'usage petits et focalises, chacun avec une seule responsabilite.</p>
 */
public interface ConsulterCanicules {

    List<Canicule> toutes();
}
