package fr.meteo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entree Spring Boot.
 *
 * <p>C'est l'unique "coquille" technique qui demarre l'application. Elle vit a
 * la racine du package {@code fr.meteo} pour que le balayage de composants
 * couvre tous les adaptateurs, sans jamais imposer Spring au coeur metier.</p>
 */
@SpringBootApplication
public class ApplicationCanicule {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationCanicule.class, args);
    }
}
