# Canicule - Architecture hexagonale et DDD avec Spring

Projet pedagogique pour comprendre **l'architecture hexagonale** (ports et
adaptateurs) et le **Domain-Driven Design (DDD)** sur un cas concret :
la **surveillance de la temperature** et la **detection des canicules**.

Le code est abondamment commente en francais (sans accents, volontairement) :
chaque classe explique QUEL concept elle illustre et POURQUOI.

> ### [Le cours en 42 diapositives](https://javakhanstudio.github.io/Spring_Hexagonal/)
>
> Chaque notion est definie avant d'etre employee, puis montree dans le code reel
> de ce depot. Navigation au clavier (`←` `→`, `Espace`), `T` pour changer de theme.
> Source : [`docs/index.html`](docs/index.html).

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
| `StationId` | Objet-valeur | Une identite typee : le compilateur refuse un identifiant de canicule |
| `ReleveTemperature` | Objet-valeur | Une mesure pour un jour donne |
| `SeuilCanicule` | Objet-valeur / politique | La regle : temperature seuil + nombre de jours |
| `StationMeteo` | Racine d'agregat | Regroupe ses releves, garantit l'invariant "un releve par jour" |
| `Canicule` | Racine d'agregat | Une periode de chaleur soutenue detectee |
| `DetecteurCanicule` | Service de domaine | Transforme une serie de releves en canicules |

---

## 2. Deux contextes delimites (bounded contexts)

Un **contexte delimite** est une zone a l'interieur de laquelle chaque mot du
metier a un sens unique. Le mot change de sens quand on franchit la frontiere.

Le mot, ici, est **"station"**. Il designe **deux modeles differents** :

| | `fr.meteo.surveillance` | `fr.meteo.maintenance` |
|---|---|---|
| Classe | `StationMeteo` | `Station` |
| Une station, c'est... | une source de releves de temperature | un equipement a etalonner |
| Invariant garde | un releve par jour | pas de mise en service sans etalonnage valide |
| Connait `Temperature` ? | oui, c'est son sujet | non, jamais |
| Connait `NumeroSerie` ? | non, jamais | oui |
| Table | `stations`, `releves` | `stations_maintenance` |

Chaque contexte definit son **propre** `StationId`. Cette duplication est
**deliberee** : partager la classe souderait les deux modeles, et changer
l'identifiant d'un cote casserait l'autre. Les deux contextes ne partagent que
la **valeur brute** de l'identifiant (un `UUID`), qui sert de correlation.

Consequence visible au bord du systeme : le meme boitier physique se consulte
par deux URL qui n'ont **aucun champ en commun**, hors l'identifiant.

```bash
GET /api/stations/{id}              -> { id, ville, codePostal }
GET /api/maintenance/stations/{id}  -> { id, numeroSerie, etat, dernierEtalonnage, etalonnagePerime }
```

Aucun des deux contextes n'est "le vrai" domaine : ils sont **freres** dans
l'arborescence, comme ils le sont dans le metier.

---

## 3. L'architecture hexagonale

Idee centrale : le **coeur metier** (le domaine) est au centre et ne depend de
**rien**. Tout ce qui est technique (web, base de donnees, notifications) est a
la peripherie et depend du centre. Les **dependances pointent vers l'interieur**.

Chaque contexte delimite possede **son propre hexagone**.

```
        COTE PILOTAGE (driving)                     COTE PILOTE (driven)
     adaptateurs entrants                          adaptateurs sortants
   +------------------------+                     +------------------------+
   |  StationController     |                     |  ...PersistenceAdapter |
   |  CaniculeController    |  --> PORTS -->      |  (JPA / H2)            |
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

Le schema des **deux hexagones de ce projet**, avec tous leurs branchements, est
sur la [diapositive 33 du cours](https://javakhanstudio.github.io/Spring_Hexagonal/#33).

---

## 4. Structure des packages

L'arborescence est une affirmation : les contextes sont freres, et seul ce qui
n'appartient a aucun d'eux vit au-dessus.

```
fr.meteo
├── ApplicationCanicule.java   <- demarrage Spring Boot, hors contexte
├── config/                    <- RACINE DE COMPOSITION, hors contexte
│   ├── ConfigurationSurveillance.java
│   ├── ConfigurationMaintenance.java
│   └── JeuDeDonneesDemo.java
│
├── surveillance/              <- CONTEXTE DELIMITE
│   ├── domaine/               (coeur metier, AUCUN import de framework)
│   │   ├── modele/            (Temperature, StationMeteo, Canicule, ...)
│   │   ├── port/entrant/      (cas d'usage : interfaces appelees de l'exterieur)
│   │   ├── port/sortant/      (depots, notificateur : besoins du domaine)
│   │   └── service/           (DetecteurCanicule)
│   ├── application/           (services applicatifs, Java pur)
│   └── adaptateur/
│       ├── entrant/web/       (controleurs REST, DTO, gestion des erreurs)
│       └── sortant/
│           ├── persistance/   (entites JPA + mappers + adaptateurs)
│           └── notification/  (alerte par journal)
│
└── maintenance/               <- CONTEXTE DELIMITE, meme forme
    ├── domaine/
    │   ├── modele/            (Station, NumeroSerie, EtatOperationnel, StationId)
    │   └── port/              (entrant/ + sortant/)
    ├── application/           (ServiceMaintenance)
    └── adaptateur/            (web + persistance)
```

Le contexte `maintenance` a un hexagone plus **petit**, pas un hexagone
**different** : il n'a simplement pas de service de domaine, aucune regle
n'echappant encore a l'agregat `Station`.

### Points remarquables

- **Le domaine et l'application n'ont aucune annotation Spring.** Ce sont les
  classes `config/Configuration*` qui les instancient et les exposent comme
  beans. La racine de composition connait les deux contextes ; aucun contexte
  ne la connait.
- **Le modele de persistance est separe du modele de domaine.** Les entites
  `...Jpa` vivent dans l'adaptateur, avec un `Mapper` entre les deux. Le domaine
  n'est jamais deforme par les contraintes de l'ORM.
- **Les agregats se referencent par identite.** Une `Canicule` porte un
  `StationId`, pas un objet `StationMeteo`.
- **Le retour depuis la base revalide les invariants.** Les mappers passent par
  une fabrique `reconstituer(...)` : une ligne corrompue ne peut pas ressusciter
  un agregat invalide.
- **Sept tests d'architecture** (`ArchitectureHexagonaleTest`, avec ArchUnit)
  cassent la construction si le domaine importe un framework, ou si un contexte
  delimite en appelle un autre. Les regles sont verifiees, pas seulement
  documentees.

---

## 5. Lancer le projet

Pre-requis : un **JDK 21**.

```bash
# construire et lancer les tests (30 tests, dont 7 d'architecture)
./mvnw test

# demarrer l'application
./mvnw spring-boot:run
```

Au demarrage, un jeu de donnees de demonstration cree une station a Lyon avec une
vague de chaleur, puis lance la detection (voir la log "ALERTE CANICULE"). La
**meme** station est enregistree dans le contexte maintenance, avec le meme UUID :
c'est la correlation entre contextes, rendue tangible.

### API REST

```bash
# 1. Creer une station
curl -X POST http://localhost:8080/api/stations \
  -H "Content-Type: application/json" \
  -d '{"ville":"Marseille","codePostal":"13000"}'
# -> 201 { "id": "....", "ville": "Marseille", "codePostal": "13000" }

# 2. Enregistrer un releve (repeter pour plusieurs jours)
curl -X POST http://localhost:8080/api/stations/{id}/releves \
  -H "Content-Type: application/json" \
  -d '{"jour":"2025-08-01","valeur":39,"unite":"CELSIUS"}'
# -> 202 Accepted   (409 Conflict si un releve existe deja pour ce jour)

# 3. Lancer la detection sur la station
curl -X POST http://localhost:8080/api/stations/{id}/detection
# -> liste des canicules detectees

# 4. Consulter toutes les canicules connues
curl http://localhost:8080/api/canicules

# 5. La MEME station, vue par le contexte maintenance
curl http://localhost:8080/api/maintenance/stations/{id}
# -> { "id": "...", "numeroSerie": "MF-004217", "etat": "HORS_SERVICE",
#      "dernierEtalonnage": "2025-06-01", "etalonnagePerime": true }
```

Console H2 (inspecter la base) : http://localhost:8080/h2-console
(URL JDBC : `jdbc:h2:mem:canicule;DB_CLOSE_DELAY=-1`, utilisateur `sa`, mot de
passe vide). Vous y verrez les tables `stations`, `releves`, `canicules` et
`stations_maintenance` : deux contextes, deux schemas, aucune cle etrangere entre eux.

---

## 6. Parcours de lecture conseille

Pour decouvrir le projet dans le bon ordre :

1. `surveillance/domaine/modele/Temperature.java` - un objet-valeur bien fait.
2. `surveillance/domaine/modele/StationMeteo.java` - un agregat et son invariant.
3. `surveillance/domaine/service/DetecteurCanicule.java` - la regle metier centrale.
4. `surveillance/domaine/port/` - les interfaces entrantes et sortantes.
5. `surveillance/application/service/` - l'orchestration des cas d'usage.
6. `maintenance/package-info.java` - pourquoi "station" veut dire autre chose ici.
7. `maintenance/domaine/modele/Station.java` - le meme mot, un autre modele.
8. `config/ConfigurationSurveillance.java` - ou Spring rencontre (enfin) le coeur.
9. `surveillance/adaptateur/` - les branchements techniques (web, JPA, notification).
10. `test/.../ArchitectureHexagonaleTest.java` - les regles rendues executables.
