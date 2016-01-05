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
public class Vol extends Interaction<VuePersonnage> {
	
	/**
	 * Cree une interaction de duel.
	 * @param arene arene
	 * @param attaquant attaquant
	 * @param defenseur defenseur
	 */
	public Vol(Arene arene, VuePersonnage attaquant, VuePersonnage defenseur) {
		super(arene, attaquant, defenseur);
	}
	
	@Override
	public void interagir() {
		try {
			Personnage pAttaquant = (Personnage) attaquant.getElement();
			int forceAttaquant = pAttaquant.getCaract(Caracteristique.FORCE);
			int perteVie = 25;
			int perteForce = 10;


			// On enlève 25 de vie au défenseur 
			arene.ajouterCaractElement(defenseur, Caracteristique.VIE, -perteVie);
			//On rajoute 25 de vie au voleur
			arene.ajouterCaractElement(attaquant, Caracteristique.VIE, +perteVie);		
			logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " vol ("
						+ perteVie + " points de vie) a " + Constantes.nomRaccourciClient(defenseur));
			
			if(forceAttaquant < 60 )
			{
				// On enlève 10 de force au défenseur 
				arene.ajouterCaractElement(defenseur, Caracteristique.FORCE, -perteForce);
				// On rajoute 10 de force au voleur
				arene.ajouterCaractElement(attaquant, Caracteristique.FORCE, +perteForce);			
				logs(Level.INFO, Constantes.nomRaccourciClient(attaquant) + " vol ("
							+ perteForce + " points de degats) a " + Constantes.nomRaccourciClient(defenseur));
			}
			// initiative
			incrementerInitiative(defenseur);
			decrementerInitiative(attaquant);
			
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'une attaque : " + e.toString());
		}
	}

	/**
	 * Incremente l'initiative du defenseur en cas de succes de l'attaque. 
	 * @param defenseur defenseur
	 * @throws RemoteException
	 */
	private void incrementerInitiative(VuePersonnage defenseur) throws RemoteException {
		arene.ajouterCaractElement(defenseur, Caracteristique.INITIATIVE, 
				Constantes.INCR_DECR_INITIATIVE_DUEL);
	}
	
	/**
	 * Decremente l'initiative de l'attaquant en cas de succes de l'attaque. 
	 * @param attaquant attaquant
	 * @throws RemoteException
	 */
	private void decrementerInitiative(VuePersonnage attaquant) throws RemoteException {
		arene.ajouterCaractElement(attaquant, Caracteristique.INITIATIVE, 
				-Constantes.INCR_DECR_INITIATIVE_DUEL);
	}
}
