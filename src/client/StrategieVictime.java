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
public class StrategieVictime extends StrategiePersonnage{
	
	public StrategieVictime (String ipArene, int port, String ipConsole, 
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
				
				if (voisins.isEmpty()) { // je n'ai pas de voisins, j'erre
					console.setPhrase("Je suis perdu...");
					arene.modifCara(refRMI, 1 , Caracteristique.POUVOIR);
					// a modifier pour chaque personnage ( temps de recharge des pouvoirs )
					if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) == 10 && console.getPersonnage().getCaract(Caracteristique.VIE) + 10 <= 100){
						arene.modifCara(refRMI, -console.getPersonnage().getCaract(Caracteristique.POUVOIR),Caracteristique.POUVOIR);
						arene.modifCara(refRMI, 10, Caracteristique.VIE);
					}
					else if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) >= 10 && console.getPersonnage().getCaract(Caracteristique.VIE) == 100){
						arene.modifCara(refRMI, -1, Caracteristique.POUVOIR);
					}
					
					
					arene.deplace(refRMI, 0); 
				} else {
					int refCible = Calculs.chercherElementProche(position, voisins);
					int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

					Element elemPlusProche = arene.elementFromRef(refCible);

					if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) { // si suffisamment proches
						// j'interagis directement
						if(elemPlusProche instanceof Potion) { // potion
							// ramassage
							if(((Potion) elemPlusProche).getArmure()){
								console.setPhrase("Je ramasse une armure");
							}else{
								console.setPhrase("Je ramasse une potion");	
							}

						} else { // personnage
							// "duel"
							console.setPhrase("Non pité Mr "+elemPlusProche.getNom()+ "ne me frappez plus !");
							arene.deplace(refRMI, 0);
						}
					}
						
					else { // si voisins, mais plus eloignes
						// je vais ne fais rien non plus, je suis une victime
						console.setPhrase("Pitié laissez moi tranquille Seigneur " + elemPlusProche.getNom()+" !");
						arene.deplace(refRMI, 0);
					}
				}
	}
		

	
}
