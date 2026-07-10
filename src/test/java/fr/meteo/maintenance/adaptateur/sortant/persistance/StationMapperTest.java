package fr.meteo.maintenance.adaptateur.sortant.persistance;

import fr.meteo.maintenance.domaine.modele.EtatOperationnel;
import fr.meteo.maintenance.domaine.modele.NumeroSerie;
import fr.meteo.maintenance.domaine.modele.Station;
import fr.meteo.maintenance.domaine.modele.StationId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Le mapper traduit entre deux REPRESENTATIONS d'un meme modele : l'aller-retour
 * ne doit donc rien perdre ni rien inventer.
 *
 * <p>C'est le test de la traduction, rendu executable. Aucun test equivalent ne
 * peut exister entre {@code StationMeteo} et {@code StationJpa} : ces deux-la
 * appartiennent a des contextes differents.</p>
 */
class StationMapperTest {

    private static final LocalDate AUJOURD_HUI = LocalDate.of(2025, 7, 1);

    @Test
    void l_aller_retour_domaine_persistance_ne_perd_rien() {
        StationId id = StationId.nouveau();
        Station origine = new Station(id, new NumeroSerie("MF-004217"), AUJOURD_HUI.minusDays(30));
        origine.mettreEnService(AUJOURD_HUI);

        Station retour = StationMapper.versDomaine(StationMapper.versJpa(origine));

        assertThat(retour.id()).isEqualTo(id);
        assertThat(retour.numeroSerie()).isEqualTo(origine.numeroSerie());
        assertThat(retour.dernierEtalonnage()).isEqualTo(origine.dernierEtalonnage());
        // l'etat mutable survit au passage en base
        assertThat(retour.etat()).isEqualTo(EtatOperationnel.EN_SERVICE);
        assertThat(retour).isEqualTo(origine); // egalite par identite
    }

    @Test
    void la_reconstitution_revalide_les_invariants() {
        StationJpa corrompu = new StationJpa(
                StationId.nouveau().valeur(), "PAS-UN-NUMERO",
                EtatOperationnel.EN_SERVICE, AUJOURD_HUI);

        // le domaine refuse de renaitre invalide, meme depuis la base
        assertThat(catchThrowable(() -> StationMapper.versDomaine(corrompu)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private static Throwable catchThrowable(Runnable action) {
        try {
            action.run();
            return null;
        } catch (Throwable t) {
            return t;
        }
    }
}
