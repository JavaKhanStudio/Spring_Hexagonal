package fr.meteo.maintenance.adaptateur.sortant.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository Spring Data du contexte maintenance : detail purement technique.
 *
 * <p>Le nom porte le contexte pour eviter une collision de bean avec le
 * {@code StationJpaRepository} de la surveillance. Deux contextes peuvent
 * legitimement vouloir le meme nom : c'est le prix de la separation.</p>
 */
public interface StationMaintenanceJpaRepository extends JpaRepository<StationJpa, UUID> {
}
