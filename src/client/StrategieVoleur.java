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
	
	protected Hashtable<Integer, VuePersonnage> personnages1 = null;

	
	public StrategieVoleur (String ipArene, int port, String ipConsole, 
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
				if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) < Constantes.POUVOIR_MAX_VOLEUR){
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
							//Si on peut se faire tuer en un coup on fui !!!
							if(elemPlusProche.getCaract(Caracteristique.FORCE) >= console.getPersonnage().getCaract(Caracteristique.VIE))
							{
								console.setPhrase("J'erre...");
								arene.deplace(refRMI, 0); 		
							}
							//Sinon on attaque 
							else
							{
								//si on ne peut pas se faire one-shot on combat !!!
								if (elemPlusProche.getCaract(Caracteristique.FORCE) < console.getPersonnage().getCaract(Caracteristique.VIE) )
								{
									// duel
									//Si le caractère pouvoir = 20 alors on peut utiliser le pouvoir ! 
									if (console.getPersonnage().getCaract(Caracteristique.POUVOIR) == Constantes.POUVOIR_MAX_VOLEUR)
									{
										//Si on peut tuer l'adversaire avec l'attaque de base on utilise pas l'attaque vol
										if (console.getPersonnage().getCaract(Caracteristique.FORCE) >= elemPlusProche.getCaract(Caracteristique.VIE) )
										{
											console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
											arene.lanceAttaque(refRMI, refCible);		
											arene.blink(refRMI, refCible);
											VuePersonnage client = personnages1.get(refRMI);
											personnages1.get(refRMI).DeuxiemeTourVoleur();
											console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
											arene.lanceAttaque(refRMI, refCible);	
										}
										//Sinon 
										else
										{
											//Si le defenseur a une vie < à 25, le vol ne sera pas opti , donc un fait un duel simple
											if(elemPlusProche.getCaract(Caracteristique.VIE) < Constantes.VOL_DE_VIE)
											{
												console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
												arene.lanceAttaque(refRMI, refCible);	
												arene.blink(refRMI, refCible);
												VuePersonnage client = personnages1.get(refRMI);
												personnages1.get(refRMI).DeuxiemeTourVoleur();
												console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
												arene.lanceAttaque(refRMI, refCible);	
											}
											//Sinon on fait l'attaque vol
											else
											{
												// Si le voleur a une vie > 75 cela ne vaut pas le coup de voler de la vie .. on ne fait pas de super attaque
												if( -console.getPersonnage().getCaract(Caracteristique.VIE) > 75)
												{
													console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
													arene.lanceAttaque(refRMI, refCible);		
													arene.blink(refRMI, refCible);
													VuePersonnage client = personnages1.get(refRMI);
													personnages1.get(refRMI).DeuxiemeTourVoleur();
													console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
													arene.lanceAttaque(refRMI, refCible);	
												}
												else
												{
													console.setPhrase("Je lance ma super attaque sur" + elemPlusProche.getNom());
													arene.LanceVol(refRMI, refCible);	
													arene.modifCara(refRMI, -console.getPersonnage().getCaract(Caracteristique.POUVOIR), 	Caracteristique.POUVOIR);
												}
											}
										}
									}
									//Sinon on fait l'attaque de base
									else
									{
										console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
										arene.lanceAttaque(refRMI, refCible);
										arene.blink(refRMI, refCible);
										VuePersonnage client = personnages1.get(refRMI);
										personnages1.get(refRMI).DeuxiemeTourVoleur();
										console.setPhrase("Je fais un duel avec " + elemPlusProche.getNom());
										arene.lanceAttaque(refRMI, refCible);		
									}
								}
								//si on peut se faire one-shot on fui !!!
								else
								{
									console.setPhrase("Je fui  " + elemPlusProche.getNom());
									arene.deplaceLoin(refRMI, refCible);
								}
							}
						}
					} else { // si voisins, mais plus eloignes
						// je vais vers le plus proche
						if(elemPlusProche instanceof Potion) { // potion
						console.setPhrase("Je vais vers mon voisin " + elemPlusProche.getNom());
						arene.deplace(refRMI, refCible);
						}
						else
						{
							if (elemPlusProche.getCaract(Caracteristique.FORCE) >= console.getPersonnage().getCaract(Caracteristique.VIE) )
							{
								console.setPhrase("Je fui  " + elemPlusProche.getNom());
								arene.deplaceLoin(refRMI, refCible);
							}
							else
							{
								console.setPhrase("Je vais vers mon voisin " + elemPlusProche.getNom());
								arene.deplace(refRMI, refCible);
							}
						}
					}
				}
	}
}
