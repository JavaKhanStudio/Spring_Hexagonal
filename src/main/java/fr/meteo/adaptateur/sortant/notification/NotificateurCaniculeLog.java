package fr.meteo.adaptateur.sortant.notification;

import fr.meteo.domaine.modele.Canicule;
import fr.meteo.domaine.port.sortant.NotificateurCanicule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * ADAPTATEUR SORTANT : implementation "journal" du port {@link NotificateurCanicule}.
 *
 * <p>C'est un detail technique interchangeable. On pourrait le remplacer par un
 * envoi d'email ou de SMS sans toucher au domaine, tant que la nouvelle classe
 * implemente le meme port. Ici on se contente d'ecrire une alerte dans la
 * console.</p>
 */
@Component
public class NotificateurCaniculeLog implements NotificateurCanicule {

    private static final Logger log = LoggerFactory.getLogger(NotificateurCaniculeLog.class);

    @Override
    public void alerter(Canicule canicule) {
        log.warn("ALERTE CANICULE a {} : {} jour(s) du {} au {}, pic a {}",
                canicule.localisation(),
                canicule.nombreDeJours(),
                canicule.dateDebut(),
                canicule.dateFin(),
                canicule.temperaturePic());
    }
}
