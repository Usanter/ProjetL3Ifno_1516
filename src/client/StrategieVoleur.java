package client;


import java.awt.Point;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Hashtable;

import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Element;
import serveur.element.Potion;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;
import client.StrategiePersonnage;


/**
 * Strategie d'un personnage. 
 */
public class StrategieVoleur extends StrategiePersonnage{
	
	//Pour pouvoir faire deux attaques de bases en 1 tour
	protected Hashtable<Integer, VuePersonnage> personnages1 = null;
	
	protected IArene arene;
	public StrategieVoleur (String ipArene, int port, String ipConsole, 
			String nom, String groupe, HashMap<Caracteristique, Integer> caracts,
			long nbTours, Point position, LoggerProjet logger)
	{
		super(ipArene, port , ipConsole,  nom, groupe, caracts , nbTours, position , logger,7);
	}
	
	
	/**
	 * Permet d'effectuer une double attaque simple
	 * @param elemPlusProche
	 * @param refCible
	 * @param refRMI
	 * @throws RemoteException
	 */
	public void DoubleAttaqueSimple (Element elemPlusProche , int refCible , int refRMI) throws RemoteException
	{
		console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
		arene.lanceAttaque(refRMI, refCible);	
		//car quand il fait la première attaque l'adversaire est poussé, il faut donc que le voleur se TP sur l'adversaire
		//pour pouvoir faire ca deuxième attaque
		arene.blink(refRMI, refCible);
		personnages1.get(refRMI).DeuxiemeTourVoleur();
		console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
		arene.lanceAttaque(refRMI, refCible);	
	}
	
	/**
	 * Lance l'attaque la plus opti ( double attaque simple ou attaque vol )
	 * @param elemPlusProche
	 * @param refCible
	 * @param refRMI
	 * @throws RemoteException
	 */
	public void StrategiAttaque (Element elemPlusProche , int refCible , int refRMI) throws RemoteException
	{
		//Si on peut tuer l'adversaire avec l'attaque de base on utilise pas l'attaque vol
		if (console.getPersonnage().getCaract(Caracteristique.FORCE) >= elemPlusProche.getCaract(Caracteristique.VIE) )
		{
			DoubleAttaqueSimple (elemPlusProche ,refCible , refRMI);
		}
		//Sinon 
		else
		{
			//Si le defenseur a une vie < à 25, le vol ne sera pas opti , donc on fait un duel simple == double attaque
			//Ou si le voleur a une vie > 75 , le vol ne sera pas opti, donc on fait un duel simple == double attaque
			if((elemPlusProche.getCaract(Caracteristique.VIE) < Constantes.VOL_DE_VIE) || (-console.getPersonnage().getCaract(Caracteristique.VIE) > 75))
			{
				DoubleAttaqueSimple (elemPlusProche ,refCible , refRMI);	
			}
			//Sinon on fait l'attaque vol
			else
			{
				console.setPhrase("Je lance ma super attaque sur" + elemPlusProche.getNom());
				arene.LanceVol(refRMI, refCible);	
				arene.modifCara(refRMI, -console.getPersonnage().getCaract(Caracteristique.POUVOIR), 	Caracteristique.POUVOIR);
			}
		}
	}
	

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
				arene = console.getArene();
				
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
				if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) < Constantes.POUVOIR_MAX_VOLEUR){
					arene.modifCara(refRMI, 1 , Caracteristique.POUVOIR);
				}
				
				Point position1 = arene.getPosition(refRMI); 
				if(arene.TestSurSpawn(refRMI , position1) == true)
				{
					console.setPhrase("Je regénère ma vie ");
					arene.RegeneVie(refRMI );
				}
				
				if (voisins.isEmpty()) { // je n'ai pas de voisins
					//Si la vie du voleur est < 100 alors on va regénérer sa vie en allant au spawn
					if (console.getPersonnage().getCaract(Caracteristique.VIE) < 100 )
					{
						console.setPhrase("Je me déplace vers le spwan ");
						arene.deplace(refRMI,new Point(Calculs.nombreAleatoire(46, 54),Calculs.nombreAleatoire(46, 54)));
					}
			//Sinon on erre
					else {
					console.setPhrase("Je cherche quelqu'un a dépouiller...");
					arene.deplace(refRMI, 0);
					}
				}
				//Si j'ai un voisin
				else {
					
					int refCible = Calculs.chercherElementProche(position, voisins);
					int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

					Element elemPlusProche = arene.elementFromRef(refCible);

					if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) 
					{ // si suffisamment proches
						// j'interagis directement
						if(elemPlusProche instanceof Potion == false) { 
							// personnage
							//Si on peut se faire tuer en un coup on fui !!!
							if(elemPlusProche.getCaract(Caracteristique.FORCE) >= console.getPersonnage().getCaract(Caracteristique.VIE))
							{
								//Si le voleur n'est pas sur le spawn on va en direction de ce dernier
								if(arene.TestSurSpawn(refRMI , position1) == false)
								{
									console.setPhrase("Je vais récupérer de la vie pour te combattre ! " + elemPlusProche.getNom());
									arene.deplace(refRMI,new Point(Calculs.nombreAleatoire(46, 54),Calculs.nombreAleatoire(46, 54)));
								}
								else
								{
									console.setPhrase("Je fuis ! " + elemPlusProche.getNom());
									arene.deplaceLoin(refRMI, refCible);
								}	
							}
							//Sinon on attaque 
							else
							{
								//Si le caractère pouvoir = 20 alors on peut utiliser le pouvoir ! 
								if (console.getPersonnage().getCaract(Caracteristique.POUVOIR) == Constantes.POUVOIR_MAX_VOLEUR)
								{
									StrategiAttaque (elemPlusProche , refCible , refRMI);
								}
								//Si le voleur n'a pas encore chargé sa super attaque vol
								// On fait l'attaque de base
								else
								{
									DoubleAttaqueSimple (elemPlusProche ,refCible , refRMI);	
								}
							}
						} 
						else { // potion
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
						}
					} //fin si j'ai un voisin avec qui je peut interagir
					else { // si voisins, mais plus eloignes
						// je vais vers le plus proche
						if(elemPlusProche instanceof Potion == false) { //Perso
							//Si le personnage est trop fort pour nous, on fui !
							if (elemPlusProche.getCaract(Caracteristique.FORCE) >= console.getPersonnage().getCaract(Caracteristique.VIE) )
							{
								//Si la vie du voleur est < 100 alors on va regénérer sa vie en allant au spawn
								if (console.getPersonnage().getCaract(Caracteristique.VIE) < 100 && arene.TestSurSpawn(refRMI , position1) == false)
								{
									console.setPhrase("Je me déplace vers le spawn ");
									arene.deplace(refRMI,new Point(Calculs.nombreAleatoire(46, 54),Calculs.nombreAleatoire(46, 54)));
								}
								else
								{
									console.setPhrase("Je fuis  " + elemPlusProche.getNom());
									arene.deplaceLoin(refRMI, refCible);
								}
							}
							//Sinon on va vers lui
							else
							{
								console.setPhrase(elemPlusProche.getNom()+" j'arrive !");
								arene.deplace(refRMI, refCible);
							}
						}
						else
						{
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
					}//Fin si j'ai un voisin mais plus eloigne
				}//Fin si j'ai un voisin
	}
}
