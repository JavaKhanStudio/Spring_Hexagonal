package fr.meteo.adaptateur.entrant.web.dto;

import fr.meteo.domaine.modele.StationId;

/**
 * DTO de sortie decrivant une station creee.
 */
public record StationReponse(String id, String ville, String codePostal) {

    public static StationReponse depuis(StationId id, String ville, String codePostal) {
        return new StationReponse(id.toString(), ville, codePostal);
    }
}
