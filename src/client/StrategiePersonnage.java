package client;


import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.controle.Console;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import utilitaires.Calculs;
import utilitaires.Constantes;

import java.math.*;

/**
 * Strategie d'un personnage. 
 */
public class StrategiePersonnage {
	
	/**
	 * Console permettant d'ajouter une phrase et de recuperer le serveur 
	 * (l'arene).
	 */
	protected Console console;
	
	protected StrategiePersonnage(LoggerProjet logger){
		logger.info("Lanceur", "Creation de la console...");
	}

	/**
	 * Cree un personnage, la console associe et sa strategie.
	 * @param ipArene ip de communication avec l'arene
	 * @param port port de communication avec l'arene
	 * @param ipConsole ip de la console du personnage
	 * @param nom nom du personnage
	 * @param groupe groupe d'etudiants du personnage
	 * @param nbTours nombre de tours pour ce personnage (si negatif, illimite)
	 * @param position position initiale du personnage dans l'arene
	 * @param logger gestionnaire de log
	 */
	public StrategiePersonnage(String ipArene, int port, String ipConsole, 
			String nom, String groupe, HashMap<Caracteristique, Integer> caracts,
			int nbTours, Point position, LoggerProjet logger) {
		this(logger);
		
		try {
			console = new Console(ipArene, port, ipConsole, this, 
					new Personnage(nom, groupe, caracts), 
					nbTours, position, logger);
			logger.info("Lanceur", "Creation de la console reussie");
			
		} catch (Exception e) {
			logger.info("Personnage", "Erreur lors de la creation de la console : \n" + e.toString());
			e.printStackTrace();
		}
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
	public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException {
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
		
		if (voisins.isEmpty()) { // je n'ai pas de voisins, j'erre
			console.setPhrase("J'erre...");
			arene.deplace(refRMI, 0); 
			
		} else {
			int refCible = Calculs.chercheElementProche(position, voisins);
			int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

			String elemPlusProche = arene.nomFromRef(refCible);

			if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) { // si suffisamment proches
				// j'interagis directement
				if (elemPlusProche == "Monstre")
				{
					console.setPhrase("J'attaque un monstre ! ");
					arene.lanceAttaque(refRMI, refCible);
				}
				else if(arene.estPotionFromRef(refCible)){ // potion
					// ramassage
					console.setPhrase("Je ramasse une potion");

					arene.ramassePotion(refRMI, refCible);			
				} else { // personnage
					// duel
					console.setPhrase("Je fais un duel avec " + elemPlusProche);
					arene.lanceAttaque(refRMI, refCible);
					arene.deplace(refRMI, refCible);
				}
				
			} else { // si voisins, mais plus eloignes
				// je vais vers le plus proche
				//Si personnage
				if (arene.estPotionFromRef(refCible) == false)
				{
					if(arene.estMonstreFromRef(refRMI) == true)
					{
						if (distPlusProche == 3)
						{
							console.setPhrase("Je vais vers un monstre et je l'attaque !");
							arene.deplace(refRMI, refCible);
							arene.lanceAttaque(refRMI, refCible);
						}
						console.setPhrase("Je vais vers un monstre !");
						arene.deplace(refRMI, refCible);
					}
					//Personnage adverse !
					else
					{
						if(distPlusProche == 4) //On attend que l'ennemie avance pour pouvoir taper en premier !
						{
							//On se heal le tenmps que le perso adverse soit à une distance de 3
							if (console.getPersonnage().getCaract(Caracteristique.VIE) < 100)
							{
								arene.lanceAutoSoin(refRMI);
							}
							else
							{
								console.setPhrase("J'attend que l'aversaire arrive ! ");
							}
						}
						if(distPlusProche == 3)
						{
							console.setPhrase("Je vais vers "  +elemPlusProche);
							arene.deplace(refRMI, refCible);
							arene.lanceAttaque(refRMI, refCible);
						}
						else{
							if(voisins.size() >= 2){
								String temp = elemPlusProche;
								voisins.remove(refCible);
								refCible = Calculs.chercheElementProche(position,voisins);
								distPlusProche = Calculs.distanceChebyshev(position,arene.getPosition(refCible));
								elemPlusProche = arene.nomFromRef(refCible);
								console.setPhrase(temp+" -> "+elemPlusProche);
							}
							arene.deplace(refRMI, refCible);
						}
					}
				}
				//Potion ! 
				else
				{
					console.setPhrase("Je vais ramasser  " + elemPlusProche);
					arene.deplace(refRMI, refCible);
				}
				//console.setPhrase("Je vais vers mon voisin " + elemPlusProche);
				//arene.deplace(refRMI, refCible);
				//arene.lanceAttaque(refRMI, refCible);
			}
		}
	}
	
	/**
	 * Fuit ( par symmetrie centrale ) la cible refCible sur l'arene arene
	 * @param refRMI personnage
	 * @param refCible ennemi
	 * @param arene arene actuelle
	 * @throws RemoteException
	 */
	void fuir(int refRMI,int refCible,IArene arene) throws RemoteException {
		arene.deplace(refRMI, new Point(arene.getPosition(refRMI).x * 2 - arene.getPosition(refCible).x,arene.getPosition(refRMI).x * 2 - arene.getPosition(refCible).x));
	}
	
	/**
	 * petit rajout de calcul de distance pour alleger le code ( eviter les arene.getposition partout )
	 * @param refRMI personnage
	 * @param refCible ennemi
	 * @param arene arene actuelle
	 * @throws RemoteException
	 * @return retourne la distance
	 */
	int get_distance(int refRMI, int refCible, IArene arene) throws RemoteException{
		return Calculs.distanceChebyshev(arene.getPosition(refRMI), arene.getPosition(refCible));
	}
	
	
	
}
