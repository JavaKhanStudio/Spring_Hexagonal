package fr.meteo.application;

import fr.meteo.domaine.modele.StationId;

/**
 * Levee par un cas d'usage quand la station demandee n'existe pas.
 * Vit dans la couche application : c'est une erreur d'orchestration, traduite
 * plus tard en 404 par l'adaptateur web (qui, lui, connait HTTP).
 */
public class StationIntrouvableException extends RuntimeException {

    public StationIntrouvableException(StationId id) {
        super("Aucune station meteo pour l'identifiant " + id);
    }
}
