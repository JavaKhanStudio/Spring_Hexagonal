package fr.meteo.surveillance.adaptateur.sortant.persistance;

import fr.meteo.surveillance.domaine.modele.Canicule;
import fr.meteo.surveillance.domaine.modele.StationId;
import fr.meteo.surveillance.domaine.port.sortant.DepotCanicule;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ADAPTATEUR SORTANT : branche le port {@link DepotCanicule} sur JPA/H2.
 */
@Component
public class CaniculePersistenceAdapter implements DepotCanicule {

    private final CaniculeJpaRepository repository;

    public CaniculePersistenceAdapter(CaniculeJpaRepository repository) {
        this.repository = repository;
    }

    @Override
    public void sauvegarder(Canicule canicule) {
        repository.save(CaniculeMapper.versJpa(canicule));
    }

    @Override
    public List<Canicule> toutes() {
        return repository.findAll().stream()
                .map(CaniculeMapper::versDomaine)
                .toList();
    }

    @Override
    public List<Canicule> parStation(StationId stationId) {
        return repository.findByStationId(stationId.valeur()).stream()
                .map(CaniculeMapper::versDomaine)
                .toList();
    }
}
