/**
 * CONTEXTE DELIMITE (Bounded Context) : la surveillance des canicules.
 *
 * <p>Dans ce contexte, le mot <b>"station"</b> designe une source de releves de
 * temperature. La classe qui le porte est
 * {@link fr.meteo.surveillance.domaine.modele.StationMeteo}, et son invariant
 * est : <i>un seul releve par jour</i>.</p>
 *
 * <p>Ce contexte est le <b>sous-domaine central</b> du produit : detecter les
 * vagues de chaleur est la raison d'etre de l'application. C'est pour cela qu'il
 * possede un hexagone complet (domaine, application, adaptateurs), la ou
 * {@code fr.meteo.maintenance} se limite pour l'instant a son domaine.</p>
 *
 * <p><b>Ce contexte n'est pas "le" domaine de l'application.</b> Il est un
 * domaine parmi deux. C'est la raison pour laquelle il vit dans
 * {@code fr.meteo.surveillance.domaine} et non dans {@code fr.meteo.domaine} :
 * un paquet {@code domaine} pose a la racine laisserait croire qu'il existe une
 * seule lecture legitime du metier, et reduirait
 * {@code fr.meteo.maintenance} au rang de module secondaire. Les deux contextes
 * sont freres dans l'arborescence parce qu'ils sont freres dans le metier.</p>
 *
 * <h2>Structure</h2>
 * <ul>
 *   <li>{@code domaine} : le coeur, du Java pur. Modele, ports, services de domaine.</li>
 *   <li>{@code application} : les services applicatifs qui orchestrent les cas d'usage.</li>
 *   <li>{@code adaptateur} : la technique, tout autour (REST, JPA, journalisation).</li>
 * </ul>
 *
 * <p>Le vocabulaire de ce contexte ({@code Temperature}, {@code SeuilCanicule},
 * {@code ReleveTemperature}) n'a pas cours dans {@code fr.meteo.maintenance}, et
 * reciproquement. Un test d'architecture le verifie dans les deux sens.</p>
 *
 * @see fr.meteo.maintenance
 */
package fr.meteo.surveillance;
