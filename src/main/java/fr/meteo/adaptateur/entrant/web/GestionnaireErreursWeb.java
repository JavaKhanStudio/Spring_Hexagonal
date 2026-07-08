package fr.meteo.adaptateur.entrant.web;

import fr.meteo.application.StationIntrouvableException;
import fr.meteo.domaine.modele.StationMeteo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ADAPTATEUR ENTRANT (transverse) : traduit les exceptions METIER en reponses HTTP.
 *
 * <p>Point d'architecture important : c'est ICI, dans la couche web, que l'on
 * connait les codes HTTP. Le domaine leve des exceptions au vocabulaire metier
 * ("releve deja present", "station introuvable") sans savoir qu'elles
 * deviendront un 409 ou un 404. La correspondance est un detail d'adaptateur.</p>
 */
@RestControllerAdvice
public class GestionnaireErreursWeb {

    /** Station demandee inexistante -> 404 Not Found. */
    @ExceptionHandler(StationIntrouvableException.class)
    public ProblemDetail stationIntrouvable(StationIntrouvableException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    /** Invariant "un releve par jour" viole -> 409 Conflict. */
    @ExceptionHandler(StationMeteo.ReleveDejaPresentException.class)
    public ProblemDetail releveDejaPresent(StationMeteo.ReleveDejaPresentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    /** Donnee invalide rejetee par le domaine (ex : zero absolu) -> 400 Bad Request. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail donneeInvalide(IllegalArgumentException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    /** Validation des DTO d'entree en echec -> 400 Bad Request. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validationEchouee(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + " : " + err.getDefaultMessage())
                .reduce((a, b) -> a + " ; " + b)
                .orElse("Requete invalide");
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, message);
    }
}
