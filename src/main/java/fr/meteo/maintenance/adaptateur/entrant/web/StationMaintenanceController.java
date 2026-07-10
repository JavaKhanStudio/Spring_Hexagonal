package fr.meteo.maintenance.adaptateur.entrant.web;

import fr.meteo.maintenance.adaptateur.entrant.web.dto.StationReponse;
import fr.meteo.maintenance.domaine.modele.StationId;
import fr.meteo.maintenance.domaine.port.entrant.ConsulterStation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

/**
 * ADAPTATEUR ENTRANT du contexte maintenance.
 *
 * <p>Le meme boitier physique est donc consultable par deux URL, qui renvoient
 * deux JSON sans champ commun :</p>
 * <pre>
 * GET /api/stations/{id}             -> vue surveillance  (ville, code postal)
 * GET /api/maintenance/stations/{id} -> vue maintenance   (numero de serie, etalonnage)
 * </pre>
 *
 * <p>C'est le signe que la frontiere de contexte va jusqu'au bord du systeme :
 * elle n'est pas qu'une convention interne au code.</p>
 */
@RestController
@RequestMapping("/api/maintenance/stations")
public class StationMaintenanceController {

    private final ConsulterStation consulterStation;

    public StationMaintenanceController(ConsulterStation consulterStation) {
        this.consulterStation = consulterStation;
    }

    /** Consulte l'etat d'un equipement. 404 s'il est inconnu de la maintenance. */
    @GetMapping("/{id}")
    public ResponseEntity<StationReponse> parId(@PathVariable String id) {
        // Le temps vient de l'adaptateur, jamais du domaine.
        LocalDate aujourdHui = LocalDate.now();

        return consulterStation.parId(StationId.de(id))
                .map(station -> StationReponse.depuis(station, aujourdHui))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
