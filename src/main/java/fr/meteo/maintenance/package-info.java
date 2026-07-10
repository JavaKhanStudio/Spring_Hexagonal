/**
 * CONTEXTE DELIMITE (Bounded Context) : la maintenance du parc de stations.
 *
 * <p>Ce paquet existe pour rendre concrete une notion souvent expliquee en
 * l'air : un <b>contexte delimite</b> est une zone a l'interieur de laquelle
 * chaque mot du metier possede un sens unique. Le mot change de sens quand on
 * franchit la frontiere.</p>
 *
 * <p>Le mot en question, ici, est <b>"station"</b>. Il designe deux modeles
 * differents selon le contexte :</p>
 *
 * <table>
 *   <caption>Le meme mot, deux modeles</caption>
 *   <tr><th></th><th>Contexte surveillance</th><th>Contexte maintenance</th></tr>
 *   <tr><td>Classe</td>
 *       <td>{@code fr.meteo.surveillance.domaine.modele.StationMeteo}</td>
 *       <td>{@code fr.meteo.maintenance.domaine.modele.Station}</td></tr>
 *   <tr><td>"Une station, c'est..."</td>
 *       <td>une source de releves de temperature</td>
 *       <td>un equipement a etalonner</td></tr>
 *   <tr><td>Invariant garde</td>
 *       <td>un releve par jour</td>
 *       <td>pas de mise en service sans etalonnage valide</td></tr>
 *   <tr><td>Sait ce qu'est une temperature ?</td>
 *       <td>oui, c'est son sujet</td>
 *       <td>non, cela ne l'interesse pas</td></tr>
 * </table>
 *
 * <p><b>Le point le plus contre-intuitif</b> : ce contexte definit son PROPRE
 * {@link fr.meteo.maintenance.domaine.modele.StationId}, alors qu'il en existe
 * deja un dans {@code fr.meteo.surveillance.domaine.modele}. Cette duplication
 * est <b>voulue</b>, pas un oubli. Partager la classe creerait une dependance
 * entre les deux modeles : changer l'identifiant de la surveillance casserait la
 * maintenance. Les deux contextes ne partagent que la <b>valeur brute</b> de
 * l'identifiant (un UUID), qui sert de correlation entre eux.</p>
 *
 * <p>Les deux contextes sont <b>freres</b> dans l'arborescence
 * ({@code fr.meteo.surveillance} et {@code fr.meteo.maintenance}) : aucun des
 * deux n'est "le vrai" domaine. Seules la racine de composition
 * ({@code fr.meteo.config}) et la classe de demarrage vivent au-dessus d'eux,
 * car elles n'appartiennent a aucun contexte.</p>
 *
 * <p>Un test d'architecture ({@code ArchitectureHexagonaleTest}) verifie qu'aucune
 * classe de ce paquet ne depend du contexte surveillance, ni l'inverse.</p>
 *
 * <p>Ce contexte possede son <b>propre hexagone complet</b> : domaine, couche
 * application, adaptateurs. Il persiste dans sa propre table
 * ({@code stations_maintenance}), avec sa propre entite JPA
 * {@code StationJpa} et son propre mapper. Rien n'est mutualise avec la
 * surveillance, pas meme la table.</p>
 *
 * <p>Consequence visible au bord du systeme : le meme boitier physique se
 * consulte par deux URL qui ne partagent aucun champ, hors l'identifiant.</p>
 * <pre>
 * GET /api/stations/{id}             -&gt; { id, ville, codePostal }
 * GET /api/maintenance/stations/{id} -&gt; { id, numeroSerie, etat, dernierEtalonnage, ... }
 * </pre>
 *
 * <p>Il n'a en revanche pas de service de domaine : aucune regle de ce contexte
 * n'echappe pour l'instant a l'agregat {@code Station}. Un hexagone plus
 * <i>petit</i>, donc, mais pas un hexagone <i>different</i>.</p>
 *
 * @see fr.meteo.surveillance
 */
package fr.meteo.maintenance;
