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
public class StrategieBarbare extends StrategiePersonnage{
	
	public StrategieBarbare (String ipArene, int port, String ipConsole, 
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
		if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) < Constantes.POUVOIR_MAX_BARBARE )
			arene.modifCara(refRMI, 1 , Caracteristique.POUVOIR);
		// si la jauge de pouvoir est au max, on la met a 0 et on augmente la force de VALEUR_POUVOIR_BARBARE
		if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) >= Constantes.POUVOIR_MAX_BARBARE && console.getPersonnage().getCaract(Caracteristique.FORCE) < Constantes.FORCE_MAX_BARBARE){
			arene.modifCara(refRMI, Constantes.VALEUR_POUVOIR_BARBARE , Caracteristique.FORCE);
			arene.modifCara(refRMI, -Constantes.POUVOIR_MAX_BARBARE, Caracteristique.POUVOIR);
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
					}
					else if(((Potion)elemPlusProche).getLife()){
						console.setPhrase("Je ramasse de la vie");
					}
					else if(((Potion)elemPlusProche).getWeapon()){
						console.setPhrase("Je ramasse une arme");
					}
					else
					{
						console.setPhrase("Je ramasse une potion");	
					}
					arene.ramassePotion(refRMI, refCible);
				} else { // personnage
					//Si le personnage est trop fort pour nous, on fui !
					if (elemPlusProche.getCaract(Caracteristique.FORCE) >= console.getPersonnage().getCaract(Caracteristique.VIE) )
					{
						//Si la vie du berseker est < 85 alors on va regénérer sa vie en allant au spawn
						if (console.getPersonnage().getCaract(Caracteristique.VIE) < 100 && !arene.TestSurSpawn(refRMI , arene.getPosition(refRMI)))
						{
							console.setPhrase("Je me déplace vers le spawn ");
							arene.deplace(refRMI, new Point(Calculs.nombreAleatoire(46, 54),Calculs.nombreAleatoire(46, 54)));
						}
						else
						{
							console.setPhrase("Je fuis  " + elemPlusProche.getNom());
							arene.deplaceLoin(refRMI, refCible);
						}
					}
					//sinon on le tape
					else{
						console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
						arene.lanceAttaque(refRMI, refCible);
					}
				}
				
			} else { // si voisins, mais plus eloignes
				if(elemPlusProche instanceof Potion){
					console.setPhrase("Je vais vers " + elemPlusProche.getNom());
					arene.deplace(refRMI, refCible);
				}
				// sinon c'est un personnage
				else{
					// si il peut nous one shot on erre histoire de faire on l'as pas vu
					if(elemPlusProche.getCaract(Caracteristique.FORCE) >= console.getPersonnage().getCaract(Caracteristique.VIE))
					{
						if(console.getPersonnage().getCaract(Caracteristique.VIE) < 85 && !arene.TestSurSpawn(refRMI , arene.getPosition(refRMI))){
							console.setPhrase("Je me déplace vers le spawn ");
							arene.deplace(refRMI, new Point(Calculs.nombreAleatoire(46, 54),Calculs.nombreAleatoire(46, 54)));
						}
						else{
							//on ne va pas vers lui
							console.setPhrase("* sifflotte .. *");
							arene.deplace(refRMI, 0);
						}
					}
					//sinon on le défonce ( on est un barbare quand meme )
					else{
						console.setPhrase(elemPlusProche.getNom()+" j'arrive prépare toi!");
						arene.deplace(refRMI, refCible);
					}
				}
				
				
			}
		}
	}

	
}
