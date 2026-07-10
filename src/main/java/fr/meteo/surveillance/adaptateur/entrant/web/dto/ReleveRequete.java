package fr.meteo.surveillance.adaptateur.entrant.web.dto;

import fr.meteo.surveillance.domaine.modele.UniteTemperature;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

/**
 * DTO d'entree pour l'enregistrement d'un releve.
 */
public record ReleveRequete(
        @NotNull(message = "Le jour est obligatoire") LocalDate jour,
        @NotNull(message = "La valeur est obligatoire") Double valeur,
        @NotNull(message = "L'unite est obligatoire (CELSIUS ou FAHRENHEIT)") UniteTemperature unite) {
}
