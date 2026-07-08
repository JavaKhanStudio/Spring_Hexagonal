# Canicule - Architecture hexagonale et DDD avec Spring

Projet pedagogique pour comprendre **l'architecture hexagonale** (ports et
adaptateurs) et le **Domain-Driven Design (DDD)** sur un cas concret :
la **surveillance de la temperature** et la **detection des canicules**.

Le code est abondamment commente en francais (sans accents, volontairement) :
chaque classe explique QUEL concept elle illustre et POURQUOI.

---

## 1. Le domaine metier

Une **canicule** n'est pas une simple donnee : c'est une regle. On la *declare*
quand la temperature franchit un seuil pendant plusieurs jours consecutifs.
C'est ce genre de regle qui rend le domaine interessant a modeliser.

### Langage omnipresent (ubiquitous language)

Le vocabulaire du code EST le vocabulaire du metier :

| Terme | Type DDD | Role |
|-------|----------|------|
| `Temperature` | Objet-valeur | Une valeur + une unite (C/F), immuable, sait se convertir et se comparer |
| `Localisation` | Objet-valeur | Ou se trouve une station |
| `ReleveTemperature` | Objet-valeur | Une mesure pour un jour donne |
| `SeuilCanicule` | Objet-valeur / politique | La regle : temperature seuil + nombre de jours |
| `StationMeteo` | Racine d'agregat | Regroupe ses releves, garantit l'invariant "un releve par jour" |
| `Canicule` | Racine d'agregat | Une periode de chaleur soutenue detectee |
| `DetecteurCanicule` | Service de domaine | Transforme une serie de releves en canicules |

---

## 2. L'architecture hexagonale

Idee centrale : le **coeur metier** (le domaine) est au centre et ne depend de
**rien**. Tout ce qui est technique (web, base de donnees, notifications) est a
la peripherie et depend du centre. Les **dependances pointent vers l'interieur**.

```
        COTE PILOTAGE (driving)                     COTE PILOTE (driven)
     adaptateurs entrants                          adaptateurs sortants
   +------------------------+                     +------------------------+
   |  StationController     |                     |  ...PersistenceAdapter |
   |  CaniculeController     |  --> PORTS -->      |  (JPA / H2)            |
   |  (REST / JSON)         |    ENTRANTS         |                        |
   +-----------+------------+        |            +-----------+------------+
               |                     v                        ^
               |            +-----------------+               |  PORTS
               +----------> |    DOMAINE      | --------------+  SORTANTS
                            |  (Java pur)     |
                 PORTS      |  modele +       |
                 ENTRANTS   |  services       |    NotificateurCaniculeLog
                            +-----------------+    (alerte)
```

- **Port entrant** : interface que l'exterieur appelle pour piloter l'appli
  (`EnregistrerReleve`, `DetecterCanicules`...). Definie par le domaine.
- **Port sortant** : interface que l'appli appelle pour atteindre l'exterieur
  (`DepotStationMeteo`, `NotificateurCanicule`...). Definie par le domaine,
  implementee par un adaptateur.
- **Adaptateur** : le code technique qui branche un port sur une techno reelle.

On peut remplacer H2 par PostgreSQL, ou REST par une file de messages, **sans
toucher une ligne du domaine**.

---

## 3. Structure des packages

La structure des dossiers rend l'hexagone visible :

```
fr.meteo
├── domaine/                  <- coeur metier, AUCUN import de framework
│   ├── modele/               (Temperature, StationMeteo, Canicule, ...)
│   ├── service/              (DetecteurCanicule)
│   └── port/
│       ├── entrant/          (cas d'usage : interfaces appelees de l'exterieur)
│       └── sortant/          (depots, notificateur : besoins du domaine)
├── application/              <- services applicatifs (orchestration), Java pur
│   └── service/
├── adaptateur/
│   ├── entrant/web/          (controleurs REST, DTO, gestion des erreurs)
│   └── sortant/
│       ├── persistance/      (entites JPA + mappers + adaptateurs)
│       └── notification/     (alerte par journal)
├── config/                   <- racine de composition (le seul lien avec Spring)
└── ApplicationCanicule.java
```

### Points remarquables

- **Le domaine et l'application n'ont aucune annotation Spring.** C'est la classe
  `config/ConfigurationHexagonale` qui les instancie et les expose comme beans.
- **Le modele de persistance est separe du modele de domaine.** Les entites
  `...Jpa` vivent dans l'adaptateur, avec un `Mapper` entre les deux. Le domaine
  n'est jamais deforme par les contraintes de l'ORM.
- **Les agregats se referencent par identite.** Une `Canicule` porte un
  `StationId`, pas un objet `StationMeteo`.
- **Un test d'architecture** (`ArchitectureHexagonaleTest`, avec ArchUnit) casse
  la construction si le domaine importe un framework. La regle est verifiee, pas
  seulement documentee.

---

## 4. Lancer le projet

Pre-requis : un JDK 21.

```bash
# construire et lancer les tests
./mvnw test

# demarrer l'application
./mvnw spring-boot:run
```

Au demarrage, un jeu de donnees de demonstration cree une station a Lyon avec une
vague de chaleur, puis lance la detection (voir la log "ALERTE CANICULE").

### API REST

```bash
# 1. Creer une station
curl -X POST http://localhost:8080/api/stations \
  -H "Content-Type: application/json" \
  -d '{"ville":"Marseille","codePostal":"13000"}'
# -> 201 { "id": "....", "ville": "Marseille", ... }

# 2. Enregistrer un releve (repeter pour plusieurs jours)
curl -X POST http://localhost:8080/api/stations/{id}/releves \
  -H "Content-Type: application/json" \
  -d '{"jour":"2025-08-01","valeur":39,"unite":"CELSIUS"}'
# -> 202 Accepted

# 3. Lancer la detection sur la station
curl -X POST http://localhost:8080/api/stations/{id}/detection
# -> liste des canicules detectees

# 4. Consulter toutes les canicules connues
curl http://localhost:8080/api/canicules
```

Console H2 (inspecter la base) : http://localhost:8080/h2-console
(URL JDBC : `jdbc:h2:mem:canicule`, utilisateur `sa`, mot de passe vide).

---

## 5. Parcours de lecture conseille

Pour decouvrir le projet dans le bon ordre :

1. `domaine/modele/Temperature.java` - un objet-valeur bien fait.
2. `domaine/modele/StationMeteo.java` - un agregat et son invariant.
3. `domaine/service/DetecteurCanicule.java` - la regle metier centrale.
4. `domaine/port/` - les interfaces entrantes et sortantes.
5. `application/service/` - l'orchestration des cas d'usage.
6. `config/ConfigurationHexagonale.java` - ou Spring rencontre (enfin) le coeur.
7. `adaptateur/` - les branchements techniques (web, JPA, notification).
8. `test/.../ArchitectureHexagonaleTest.java` - les regles rendues executables.
```
# Spring_Hexagonal
