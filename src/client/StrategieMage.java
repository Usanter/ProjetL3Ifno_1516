package client;


import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;
import client.StrategiePersonnage;

/**
 * Strategie d'un personnage. 
 */
public class StrategieMage extends StrategiePersonnage{
	
	public StrategieMage (String ipArene, int port, String ipConsole, 
			String nom, String groupe, HashMap<Caracteristique, Integer> caracts,
			long nbTours, Point position, LoggerProjet logger)
	{
		super(ipArene, port , ipConsole,  nom, groupe, caracts , nbTours, position , logger);
	}
	// TODO etablir une strategie afin d'evoluer dans l'arene de combat
	// une proposition de strategie (simple) est donnee ci-dessous
	/** 
	 * Decrit la strategie.
	 * Les methodes pour evoluer dans le jeu doivent etre les methodes RMI
	 * de Arene et de ConsolePersonnage. 
	 * @param voisins element voisins de cet element (elements qu'il voit)
	 * @throws RemoteException
	 */
	public void strategie(HashMap<Integer, Point> voisins) throws RemoteException 
	{
		// arene
				IArene arene = console.getArene();
				
				// reference RMI de l'element courant
				int refRMI = 0;
				
				// position de l'element courant
				Point position = null;
				
				try {
					refRMI = console.getRefRMI();
					position = arene.getPosition(refRMI);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				// On incrémente le caractère pouvoir à chaque tour du joueur
				if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) < 20){
					arene.modifCara(refRMI, 1 , Caracteristique.POUVOIR);
				}
				if (voisins.isEmpty()) { // je n'ai pas de voisins, j'erre
					console.setPhrase("J'erre...");
				
					
					
					arene.deplace(refRMI, 0); 
				} else {
					int refCible = Calculs.chercherElementProche(position, voisins);
					int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

					Element elemPlusProche = arene.elementFromRef(refCible);
					//Si enemis dans le rayon d'action du pouvoir et pouvoir dispo alors attaque
					if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION_GRANDE 
					&& console.getPersonnage().getCaract(Caracteristique.POUVOIR) > Constantes.POUVOIR_MAX_MAGE ){
						if(!(elemPlusProche instanceof Potion)){
							//Duel a distance
							console.setPhrase("Je lance ma boule de feu sur  " + elemPlusProche.getNom());
							arene.lanceBouleDeFeu(refRMI, refCible);
							// a modifier pour chaque personnage ( temps de recharge des pouvoirs )
				
							arene.modifCara(refRMI, -console.getPersonnage().getCaract(Caracteristique.POUVOIR), Caracteristique.POUVOIR);
							
						}
					}

					if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) { // si suffisamment proches
						// j'interagis directement
						if(elemPlusProche instanceof Potion) { // potion
							// ramassage
							console.setPhrase("Je ramasse une potion");
							arene.ramassePotion(refRMI, refCible);

						} else { // personnage
							// duel
							console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
							arene.lanceAttaque(refRMI, refCible);
						}
						
					} else { // si voisins, mais plus eloignes
						if(elemPlusProche.getCaract(Caracteristique.FORCE) >= console.getPersonnage().getCaract(Caracteristique.VIE))
						{
							//on ne va pas vers lui
							console.setPhrase(elemPlusProche.getNom()+" a l'air fort, je vais lui faire croire que je suis sans défence...");
							arene.deplaceLoin(refRMI, refCible);
						}
						//sinon on le bat on va vers lui
						else{
							console.setPhrase(elemPlusProche.getNom()+" va sentir ma colere ...");
							arene.deplace(refRMI, refCible);
						}
					}
				}
	}
		

	
}
