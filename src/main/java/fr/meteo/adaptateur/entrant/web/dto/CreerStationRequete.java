package fr.meteo.adaptateur.entrant.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO d'entree (couche web). Distinct du domaine : il porte les contraintes de
 * VALIDATION D'ENTREE (format, presence) et l'annotation JSON, pas les regles
 * metier. Le controleur le traduit ensuite en objets du domaine.
 */
public record CreerStationRequete(
        @NotBlank(message = "La ville est obligatoire") String ville,
        @NotBlank(message = "Le code postal est obligatoire") String codePostal) {
}
