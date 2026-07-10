package fr.meteo.surveillance.adaptateur.sortant.persistance;

import fr.meteo.surveillance.domaine.modele.StationId;
import fr.meteo.surveillance.domaine.modele.StationMeteo;
import fr.meteo.surveillance.domaine.port.sortant.DepotStationMeteo;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * ADAPTATEUR SORTANT : branche le port {@link DepotStationMeteo} sur JPA/H2.
 *
 * <p>C'est la seule classe qui connait a la fois le domaine et la technique de
 * stockage. Elle recoit/rend des objets du DOMAINE ; la conversion vers les
 * entites JPA est cachee derriere le mapper. Le service applicatif qui l'utilise
 * ne voit que l'interface du port.</p>
 */
@Component
public class StationMeteoPersistenceAdapter implements DepotStationMeteo {

    private final StationJpaRepository repository;

    public StationMeteoPersistenceAdapter(StationJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void sauvegarder(StationMeteo station) {
        repository.save(StationMeteoMapper.versJpa(station));
    }

    /**
     * Le chargement ET la traduction vers le domaine se font dans une meme
     * transaction : le mapper parcourt la collection "releves" (chargee a la
     * demande par JPA), ce qui exige une session ouverte. Sans {@code @Transactional},
     * on obtiendrait la fameuse {@code LazyInitializationException}. La frontiere
     * transactionnelle pourrait aussi vivre au niveau du cas d'usage ; on la place
     * ici pour garder la couche application en Java pur.
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<StationMeteo> parId(StationId id) {
        return repository.findById(id.valeur())
                .map(StationMeteoMapper::versDomaine);
    }
}
