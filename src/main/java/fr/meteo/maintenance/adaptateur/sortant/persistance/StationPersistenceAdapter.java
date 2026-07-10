package fr.meteo.maintenance.adaptateur.sortant.persistance;

import fr.meteo.maintenance.domaine.modele.Station;
import fr.meteo.maintenance.domaine.modele.StationId;
import fr.meteo.maintenance.domaine.port.sortant.DepotStation;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * ADAPTATEUR SORTANT du contexte maintenance : branche {@link DepotStation} sur JPA/H2.
 *
 * <p>Chaque contexte a son propre hexagone, donc son propre adaptateur de
 * persistance, sa propre table, son propre mapper. Rien n'est mutualise : c'est
 * ce qui leur permet d'evoluer separement.</p>
 */
@Component
public class StationPersistenceAdapter implements DepotStation {

    private final StationMaintenanceJpaRepository repository;

    public StationPersistenceAdapter(StationMaintenanceJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void sauvegarder(Station station) {
        repository.save(StationMapper.versJpa(station));
    }

    @Override
    public Optional<Station> parId(StationId id) {
        return repository.findById(id.valeur())
                .map(StationMapper::versDomaine);
    }
}
