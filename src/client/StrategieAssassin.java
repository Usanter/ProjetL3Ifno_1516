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
public class StrategieAssassin extends StrategiePersonnage{
	
	public StrategieAssassin (String ipArene, int port, String ipConsole, 
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
	public void strategie(HashMap<Integer, Point> voisins) throws RemoteException {
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
		
		//on augmente la jauge de pouvoir si elle est inferieure au max
		if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) < Constantes.POUVOIR_MAX_ASSASSIN )
			arene.modifCara(refRMI, 1 , Caracteristique.POUVOIR);
		
		if (voisins.isEmpty()) { // je n'ai pas de voisins, j'erre
			console.setPhrase("J'erre...");
			arene.deplace(refRMI, 0); 
		} else {
			int refCible = Calculs.chercherElementProche(position, voisins);
			int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));
			
			Element elemPlusProche = arene.elementFromRef(refCible);
			
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
				if(elemPlusProche instanceof Potion){
					console.setPhrase("Je vais vers la potion " + elemPlusProche.getNom());
					arene.deplace(refRMI, refCible);
				}
				// sinon c'est un personnage
				else{
					// si il peut nous one shot on se barre
					if(elemPlusProche.getCaract(Caracteristique.VIE) <= console.getPersonnage().getCaract(Caracteristique.FORCE)
							&& console.getPersonnage().getCaract(Caracteristique.POUVOIR) >= Constantes.POUVOIR_MAX_ASSASSIN){
						console.setPhrase("Je suis parmi les ombres ...");
						//restat du pouvoir
						arene.modifCara(refRMI, -console.getPersonnage().getCaract(Caracteristique.POUVOIR), 	Caracteristique.POUVOIR);
						//on se tp sur lui et on le tape
						arene.blink(refRMI, refCible);
						arene.lanceAttaque(refRMI, refCible);
					}
					else if(elemPlusProche.getCaract(Caracteristique.FORCE) >= console.getPersonnage().getCaract(Caracteristique.VIE))
					{
						//on le fuit
						console.setPhrase(elemPlusProche.getNom()+" a l'air fort, je vais lui faire croire que je suis sans défence...");
						arene.deplaceLoin(refRMI, refCible);
					}
					//sinon on tente le coup
					else{
						//si on peut se tp on le fait
						if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) >= Constantes.POUVOIR_MAX_ASSASSIN){

							arene.blink(refRMI, refCible);
							arene.modifCara(refRMI, -console.getPersonnage().getCaract(Caracteristique.POUVOIR), 	Caracteristique.POUVOIR);
						}
						//sinon
						else
						console.setPhrase(elemPlusProche.getNom()+" ne me verra jamais venir...");
						arene.deplace(refRMI, refCible);
					}
				}
				
				
			}
		}
	}

	
}
