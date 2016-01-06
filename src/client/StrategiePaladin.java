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
public class StrategiePaladin extends StrategiePersonnage{
	
	public StrategiePaladin (String ipArene, int port, String ipConsole, 
			String nom, String groupe, HashMap<Caracteristique, Integer> caracts,
			long nbTours, Point position, LoggerProjet logger)
	{
		super(ipArene, port , ipConsole,  nom, groupe, caracts , nbTours, position , logger,4);
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
				if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) < 10){
					arene.modifCara(refRMI, 1 , Caracteristique.POUVOIR);
				}
				
				if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) == 10 
						&& console.getPersonnage().getCaract(Caracteristique.VIE) <= 90){
					arene.modifCara(refRMI, -console.getPersonnage().getCaract(Caracteristique.POUVOIR),Caracteristique.POUVOIR);
					arene.modifCara(refRMI, 10, Caracteristique.VIE);
				}
				
				if(arene.TestSurSpawn(refRMI, arene.getPosition(refRMI))){
		        	arene.RegeneVie(refRMI);
		        }
				
				if (voisins.isEmpty()) { // je n'ai pas de voisins, j'erre
					if(console.getPersonnage().getCaract(Caracteristique.VIE) < Constantes.VIE_GO_SPAWN){
	        			console.setPhrase("Je me sens faible, je vais aller me soigner.");
	        			arene.deplace(refRMI, new Point(Calculs.nombreAleatoire(46, 54),Calculs.nombreAleatoire(46, 54)));
	        		}
	        		else{
	        			console.setPhrase("J'erre...");
	            		arene.deplace(refRMI, 0);
	            	} 
				} 
				// si j'ai des voisins
				else {
					int refCible = Calculs.chercherElementProche(position, voisins);
					int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

					Element elemPlusProche = arene.elementFromRef(refCible);

					if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) { // si suffisamment proches
						// j'interagis directement
						if(elemPlusProche instanceof Potion) { // potion
							// ramassage
							if(((Potion) elemPlusProche).getArmure()){
								console.setPhrase("Je ramasse une armure");
							}
							else if(((Potion)elemPlusProche).getLife()){
								console.setPhrase("Je ramasse de la vie");
							}
							else if(((Potion)elemPlusProche).getWeapon()){
								console.setPhrase("Je ramasse une arme");
							}
							else{
								console.setPhrase("Je ramasse une potion");	
							}
							arene.ramassePotion(refRMI, refCible);
						} else { // personnage
							// duel
							
							if(console.getPersonnage().getCaract(Caracteristique.VIE) > 50){
								console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom()+",en garde!");
								arene.lanceAttaque(refRMI, refCible);
							}
							else{
								//on ne va pas vers lui
								console.setPhrase(elemPlusProche.getNom()+" est trop fort, vaut mieux attendre de se regenerer");
								arene.deplaceLoin(refRMI, refCible);
							}
						}
						
					} else { // si voisins, mais plus eloignes
						if(elemPlusProche instanceof Potion){
		                	// potion
							//Si la potion peut nous tuer on ne va pas la chercher
							if(elemPlusProche.getCaract(Caracteristique.VIE ) == -console.getPersonnage().getCaract(Caracteristique.VIE))
							{
								console.setPhrase( elemPlusProche.getNom()+ " je ne viens pas te chercher ");
								arene.deplaceLoin(refRMI, refCible);
							}
							else
							{
								console.setPhrase( elemPlusProche.getNom()+ " je viens te chercher !");
								arene.deplace(refRMI, refCible);
							}
		                }
		                // sinon c'est un personnage, on va vers lui
						else{
							console.setPhrase(elemPlusProche.getNom()+" j'arrive !");
							arene.deplace(refRMI, refCible);
						}
					}
				}
	}
		

	
}
