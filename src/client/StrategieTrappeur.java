package client;


import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;

import client.controle.Console;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Personnage;
import serveur.element.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;
import client.StrategiePersonnage;
import serveur.interaction.LancePotionTrappeur;
/**
 * Strategie d'un personnage. 
 */
public class StrategieTrappeur extends StrategiePersonnage{
	
	public String IP;
	public int port;
	
	public StrategieTrappeur (String ipArene, int port, String ipConsole, 
			String nom, String groupe, HashMap<Caracteristique, Integer> caracts,
			long nbTours, Point position, LoggerProjet logger)
	{
		super(ipArene, port , ipConsole,  nom, groupe, caracts , nbTours, position , logger);
		this.IP = ipArene;
		this.port = port;
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
				if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) < Constantes.POUVOIR_MAX_TRAPPEUR){
					arene.modifCara(refRMI, 1 , Caracteristique.POUVOIR);
				}
				
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
							if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) == Constantes.POUVOIR_MAX_TRAPPEUR)
							{
								console.setPhrase("Je fuis et je met une potion ! ");
								LancePotionTrappeur lancepotion = new LancePotionTrappeur ();
								lancepotion.LancePotion (port, IP, refRMI); 
								arene.modifCara(refRMI, -console.getPersonnage().getCaract(Caracteristique.POUVOIR), 	Caracteristique.POUVOIR);
								arene.deplaceLoin(refRMI, refCible);
							}
							console.setPhrase("Je fuis ! ");
							arene.deplaceLoin(refRMI, refCible); 
						}
						
					} else { // si voisins, mais plus eloignes
						// je vais vers le plus proche
						if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) == Constantes.POUVOIR_MAX_TRAPPEUR)
						{
							console.setPhrase("Je fuis et je met une potion ! ");
							LancePotionTrappeur lancepotion = new LancePotionTrappeur ();
							lancepotion.LancePotion (port, IP, refRMI); 
							arene.modifCara(refRMI, -console.getPersonnage().getCaract(Caracteristique.POUVOIR), 	Caracteristique.POUVOIR);
							arene.deplaceLoin(refRMI, refCible);
						}
						console.setPhrase("Je fuis ! ");
						arene.deplace(refRMI, 0); 
					}
				}
	}	
}
