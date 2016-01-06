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
import serveur.element.Personnage;


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
		super(ipArene, port , ipConsole,  nom, groupe, caracts , nbTours, position , logger);
	}
	
	/**
	 * Permet de savoir si le point est dans les limites du spwan de regénération de vie
	 * @param refRMI reference RMI
	 * @param position position de l'élément
	 * @return boolean
	 */
	public boolean TestSurSpawn (int refRMI , Point position)  throws RemoteException
	{	 
		if(( position.x >= 45 && position.x <= 55 )&&
				( position.y >= 45 && position.y <= 55))
		{
			return true;
		}
		return false;
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
		VuePersonnage client = personnages1.get(refRMI);
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
	public void ChoixAttaque (Element elemPlusProche , int refCible , int refRMI) throws RemoteException
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
				
				if (voisins.isEmpty()) { // je n'ai pas de voisins
					//Si la vie du voleur est > 50 alors on va regénérer sa vie en allant au spawn
					if (console.getPersonnage().getCaract(Caracteristique.VIE) < 50)
					{
						Point position1 = arene.getPosition(refRMI); 
						//Si le voleur est sur le spawn on regénère sa vie
						if(TestSurSpawn(refRMI , position1) == true)
						{
							console.setPhrase("Je regénère ma vie ");
							arene.RegeneVie(refRMI );
							arene.deplace(refRMI, 0); 
						}
						//Si il n'est pas sur le spawn il va en direction du spawn
						else
						{
						console.setPhrase("Je me déplace vers le spwan ");
						arene.deplaceSpawn(refRMI, position); 
						}
					}
					//Sinon, si le voleur a une vie >= 50 alors on erre
					else {
					console.setPhrase("Je cherche quelqu'un a dépouiller...");
					arene.deplace(refRMI, 0); }
				}
				//Si j'ai un voisin
				else {
					int refCible = Calculs.chercherElementProche(position, voisins);
					int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

					Element elemPlusProche = arene.elementFromRef(refCible);

					if(distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) 
					{ // si suffisamment proches
						// j'interagis directement
						if(elemPlusProche instanceof Potion) { // potion
							// ramassage
							if(((Potion) elemPlusProche).getArmure()){
								console.setPhrase("Je ramasse une armure");
							}else{
								console.setPhrase("Je ramasse une potion");	
							}
						} 
						else { 
							// personnage
							//Si on peut se faire tuer en un coup on fui !!!
							if(elemPlusProche.getCaract(Caracteristique.FORCE) >= console.getPersonnage().getCaract(Caracteristique.VIE))
							{
								console.setPhrase("Je cherche quelqu'un à depouiller...");
								arene.deplace(refRMI, 0); 		
							}
							//Sinon on attaque 
							else
							{
								//Si le caractère pouvoir = 20 alors on peut utiliser le pouvoir ! 
								if (console.getPersonnage().getCaract(Caracteristique.POUVOIR) == Constantes.POUVOIR_MAX_VOLEUR)
								{
									ChoixAttaque (elemPlusProche , refCible , refRMI);
								}
								//Si le voleur n'a pas encore chargé sa super attaque vol
								// On fait l'attaque de base
								else
								{
									DoubleAttaqueSimple (elemPlusProche ,refCible , refRMI);	
								}
							}
						}
					} //fin si j'ai un voisin avec qui je peut interagir
					else { // si voisins, mais plus eloignes
						// je vais vers le plus proche
						if(elemPlusProche instanceof Potion) { // potion
						console.setPhrase( elemPlusProche.getNom()+ " je viens te chercher !");
						arene.deplace(refRMI, refCible);
						}
						else
						{
							//Si le personnage est trop fort pour nous, on fui !
							if (elemPlusProche.getCaract(Caracteristique.FORCE) >= console.getPersonnage().getCaract(Caracteristique.VIE) )
							{
								console.setPhrase("Je fui  " + elemPlusProche.getNom());
								arene.deplaceLoin(refRMI, refCible);
							}
							//Sinon on va vers lui
							else
							{
								console.setPhrase(elemPlusProche.getNom()+" j'arrive !");
								arene.deplace(refRMI, refCible);
							}
						}
					}//Fin si j'ai nu voisin mais plus eloigne
				}//Fin si j'ai un voisin
	}
}
