package fr.meteo.maintenance.domaine.modele;

/**
 * Etat d'un equipement dans le contexte maintenance.
 *
 * <p>Encore du vocabulaire absent du contexte surveillance : le detecteur de
 * canicules ne sait pas ce qu'est un etalonnage, et n'a pas a le savoir.</p>
 */
public enum EtatOperationnel {

    /** En fonctionnement : ses releves sont exploitables. */
    EN_SERVICE,

    /** Immobilisee : panne, ou etalonnage perime. */
    HORS_SERVICE,

    /** Sur le banc de calibration, temporairement indisponible. */
    EN_ETALONNAGE
}
