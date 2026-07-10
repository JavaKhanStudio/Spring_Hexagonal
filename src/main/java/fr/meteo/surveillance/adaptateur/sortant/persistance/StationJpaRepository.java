package fr.meteo.surveillance.adaptateur.sortant.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Repository Spring Data : detail purement technique de l'adaptateur.
 * Il n'est JAMAIS expose au domaine ; seul le port {@code DepotStationMeteo}
 * l'est. Spring genere l'implementation a l'execution.
 */
public interface StationJpaRepository extends JpaRepository<StationMeteoJpa, UUID> {
}
