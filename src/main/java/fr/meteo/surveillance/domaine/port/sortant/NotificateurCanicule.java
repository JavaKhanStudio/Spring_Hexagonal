package fr.meteo.surveillance.domaine.port.sortant;

import fr.meteo.surveillance.domaine.modele.Canicule;

/**
 * PORT SORTANT : notification d'une alerte canicule.
 *
 * <p>Le domaine sait qu'il faut "alerter" quand une canicule est detectee, mais
 * ignore COMMENT (log, email, SMS, webhook...). Cette interface exprime
 * l'intention ; l'adaptateur choisit le moyen. On peut ainsi ajouter un canal
 * d'alerte sans modifier le coeur metier (principe ouvert/ferme).</p>
 */
public interface NotificateurCanicule {

    void alerter(Canicule canicule);
}
