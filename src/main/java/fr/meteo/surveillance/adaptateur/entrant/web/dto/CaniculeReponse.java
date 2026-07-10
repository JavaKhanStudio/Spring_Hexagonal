package fr.meteo.surveillance.adaptateur.entrant.web.dto;

import fr.meteo.surveillance.domaine.modele.Canicule;

import java.time.LocalDate;

/**
 * DTO de sortie decrivant une canicule. On expose une vue "aplatie" et pratique
 * pour le client (ville, pic en Celsius, nombre de jours calcule), sans laisser
 * fuiter les objets du domaine hors de l'application.
 */
public record CaniculeReponse(
        String id,
        String stationId,
        String ville,
        String codePostal,
        LocalDate dateDebut,
        LocalDate dateFin,
        long nombreJours,
        double picCelsius) {

    public static CaniculeReponse depuis(Canicule c) {
        return new CaniculeReponse(
                c.id().toString(),
                c.stationId().toString(),
                c.localisation().ville(),
                c.localisation().codePostal(),
                c.dateDebut(),
                c.dateFin(),
                c.nombreDeJours(),
                c.temperaturePic().valeurEnCelsius());
    }
}
