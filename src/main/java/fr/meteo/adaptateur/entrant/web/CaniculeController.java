package fr.meteo.adaptateur.entrant.web;

import fr.meteo.adaptateur.entrant.web.dto.CaniculeReponse;
import fr.meteo.domaine.modele.StationId;
import fr.meteo.domaine.port.entrant.ConsulterCanicules;
import fr.meteo.domaine.port.entrant.DetecterCanicules;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ADAPTATEUR ENTRANT pour la detection et la consultation des canicules.
 */
@RestController
@RequestMapping("/api")
public class CaniculeController {

    private final DetecterCanicules detecterCanicules;
    private final ConsulterCanicules consulterCanicules;

    public CaniculeController(DetecterCanicules detecterCanicules, ConsulterCanicules consulterCanicules) {
        this.detecterCanicules = detecterCanicules;
        this.consulterCanicules = consulterCanicules;
    }

    /** Lance la detection sur une station. POST /api/stations/{id}/detection */
    @PostMapping("/stations/{id}/detection")
    public List<CaniculeReponse> detecter(@PathVariable String id) {
        return detecterCanicules.detecterPour(StationId.de(id)).stream()
                .map(CaniculeReponse::depuis)
                .toList();
    }

    /** Liste toutes les canicules connues. GET /api/canicules */
    @GetMapping("/canicules")
    public List<CaniculeReponse> toutes() {
        return consulterCanicules.toutes().stream()
                .map(CaniculeReponse::depuis)
                .toList();
    }
}
