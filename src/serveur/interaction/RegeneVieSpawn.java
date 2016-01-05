package serveur.interaction;

import java.awt.Point;
import java.rmi.RemoteException;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

/**
 * Represente un duel entre deux personnages.
 *
 */
public class RegeneVieSpawn extends Interaction<VuePersonnage> {
	
	/**
	 * Cree une interaction de duel.
	 * @param arene arene
	 * @param attaquant attaquant
	 * @param defenseur defenseur
	 */
	public RegeneVieSpawn(Arene arene, VuePersonnage personnage) {
		super(arene, personnage);
	}
	
	@Override
	public void interagir() {
		try {
			Personnage pAttaquant = (Personnage) attaquant.getElement();

			// On enlève 25 de vie au défenseur 
			arene.ajouterCaractElement(attaquant, Caracteristique.VIE, +2);

		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'une attaque : " + e.toString());
		}
	}
}
