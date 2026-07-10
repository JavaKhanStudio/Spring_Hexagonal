package fr.meteo.surveillance.adaptateur.entrant.web;

import fr.meteo.surveillance.adaptateur.entrant.web.dto.CreerStationRequete;
import fr.meteo.surveillance.adaptateur.entrant.web.dto.ReleveRequete;
import fr.meteo.surveillance.adaptateur.entrant.web.dto.StationReponse;
import fr.meteo.surveillance.domaine.modele.Localisation;
import fr.meteo.surveillance.domaine.modele.ReleveTemperature;
import fr.meteo.surveillance.domaine.modele.StationId;
import fr.meteo.surveillance.domaine.modele.Temperature;
import fr.meteo.surveillance.domaine.port.entrant.CreerStation;
import fr.meteo.surveillance.domaine.port.entrant.EnregistrerReleve;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ADAPTATEUR ENTRANT : traduit le HTTP en appels de PORTS ENTRANTS.
 *
 * <p>Role d'un adaptateur entrant : parler la technologie d'entree (ici REST/JSON)
 * et la convertir dans le langage du domaine, puis appeler un port. Il ne
 * contient aucune regle metier. Il depend d'interfaces ({@link CreerStation},
 * {@link EnregistrerReleve}), pas des services concrets : on pourrait rebrancher
 * une autre implementation sans le modifier.</p>
 */
@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final CreerStation creerStation;
    private final EnregistrerReleve enregistrerReleve;

    public StationController(CreerStation creerStation, EnregistrerReleve enregistrerReleve) {
        this.creerStation = creerStation;
        this.enregistrerReleve = enregistrerReleve;
    }

    /** Cree une station. POST /api/stations */
    @PostMapping
    public ResponseEntity<StationReponse> creer(@Valid @RequestBody CreerStationRequete requete) {
        StationId id = creerStation.creer(new Localisation(requete.ville(), requete.codePostal()));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(StationReponse.depuis(id, requete.ville(), requete.codePostal()));
    }

    /** Enregistre un releve. POST /api/stations/{id}/releves */
    @PostMapping("/{id}/releves")
    public ResponseEntity<Void> enregistrerReleve(@PathVariable String id,
                                                  @Valid @RequestBody ReleveRequete requete) {
        // Traduction DTO web -> objets du domaine.
        Temperature temperature = Temperature.de(requete.valeur(), requete.unite());
        ReleveTemperature releve = new ReleveTemperature(requete.jour(), temperature);

        enregistrerReleve.enregistrer(new EnregistrerReleve.Commande(StationId.de(id), releve));
        return ResponseEntity.accepted().build();
    }
}
