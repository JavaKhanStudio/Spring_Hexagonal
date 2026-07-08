package fr.meteo.adaptateur.sortant.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository Spring Data des canicules.
 */
public interface CaniculeJpaRepository extends JpaRepository<CaniculeJpa, UUID> {

    List<CaniculeJpa> findByStationId(UUID stationId);
}
