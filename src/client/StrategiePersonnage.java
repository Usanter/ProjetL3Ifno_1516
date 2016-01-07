package client;


import java.awt.Point;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

import client.controle.Console;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import utilitaires.Calculs;
import utilitaires.Constantes;


/**
 * Strategie d'un personnage. 
 */
public class StrategiePersonnage {
  /**
   * BlackList des personnages
   */
public static ArrayList<Integer> blacklist;
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
    blacklist = new ArrayList<Integer>();
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
              // ATTETION RAMASSER QUE SI POPO GOOOD !!!!!!!!
              console.setPhrase("Je ramasse une potion");
              arene.ramassePotion(refRMI, refCible);      
            } else { // personnage
              // ATTATION !!!!! ATTAQUER QUE SI ON EST SUR DE LE TUER
              console.setPhrase("Je fais un duel avec " + elemPlusProche);
              arene.lanceAttaque(refRMI, refCible);
              arene.deplace(refRMI, refCible);
            }
            
          } 
        else
        {
            // chercher une cible
            refCible = get_nearest_monster(arene, voisins, refRMI);
            if(refCible == 0){
              refCible = get_nearest_potion(arene, voisins, refRMI, blacklist);
              if(refCible == 0){
                refCible = Calculs.chercheElementProche(position, voisins);
              }
            }
            distPlusProche = Calculs.distanceChebyshev(position,arene.getPosition(refCible));
            elemPlusProche = arene.nomFromRef(refCible);      
            //cible trouvée  

	        // je vais vers le plus proche
	        //Si Mond=stre
	          if(arene.estMonstreFromRef(refRMI) )
	          {
	            if (distPlusProche == 3)
	            {
	              console.setPhrase("Je vais vers un monstre et je l'attaque !");
	              arene.deplace(refRMI, refCible);
	              arene.lanceAttaque(refRMI, refCible);
	            }
	            console.setPhrase("Je vais vers un monstre !");
	            arene.deplace(refRMI, refCible);
	          }//Fin de si monstre
	          //Personnage adverse !
	          if(arene.estPersonnageFromRef(refRMI) )
	          {
	            if(distPlusProche == 4) //On attend que l'ennemie avance pour pouvoir taper en premier !
	            {
		              //On se heal le temps que le perso adverse soit à une distance de 3
		              if (console.getPersonnage().getCaract(Caracteristique.VIE) < 100)
		              {
		                arene.lanceAutoSoin(refRMI);
		                console.setPhrase("Je me soigne ...");
		              }
		              else
		              {
		                console.setPhrase("J'attend que l'aversaire arrive ! ");
		              }
	            }
	            else if(distPlusProche == 3)
	            {
	              console.setPhrase("Je vais vers "  +elemPlusProche + " pour l'attaquer");
	              arene.deplace(refRMI, refCible);
	              arene.lanceAttaque(refRMI, refCible);
	            }
	            else
	            {
	              //ICI IL FAUDRAIT FAIRE CLAIVOYANCE POUR SAVOIR SI ON LE FUI OU PAS
	              console.setPhrase("Je vais vers "  +elemPlusProche );
	              arene.deplace(refRMI, refCible);
	            }
	          }//Fin de si personnage
	        //Potion ! 
	        else
	        {
	          console.setPhrase("Je vais ramasser  " + elemPlusProche);
	          arene.deplace(refRMI, refCible);
	        }
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
  
  int get_nearest_monster(IArene arene,HashMap<Integer, Point> voisins, int refRMI )throws RemoteException{
    int refCible = Calculs.chercheElementProche(arene.getPosition(refRMI),voisins);
    while(!arene.estMonstreFromRef(refCible) && voisins.size() >= 2)
    {
      voisins.remove(refCible);
      refCible = Calculs.chercheElementProche(arene.getPosition(refRMI),voisins);
    }
    if(!arene.estMonstreFromRef(refCible)) refCible = 0;
    return refCible;
  }
  
  int get_nearest_potion(IArene arene,HashMap<Integer, Point> voisins, int refRMI, ArrayList<Integer> blacklist  )throws RemoteException{
	    int refCible = Calculs.chercheElementProche(arene.getPosition(refRMI),voisins);
	    while(!arene.estPotionFromRef(refCible) && voisins.size() >= 2)
	      if(!blacklist.contains(refCible)){
	        voisins.remove(refCible);
	        refCible = Calculs.chercheElementProche(arene.getPosition(refRMI),voisins);
	      }
	    if(!arene.estPotionFromRef(refCible)) refCible = 0;
	    return refCible;
	  }
  
  int get_nearest_player(IArene arene,HashMap<Integer, Point> voisins, int refRMI )throws RemoteException{
    int refCible = Calculs.chercheElementProche(arene.getPosition(refRMI),voisins);
    while(!arene.estPersonnageFromRef(refCible) && voisins.size() >= 2)
    {
      voisins.remove(refCible);
      refCible = Calculs.chercheElementProche(arene.getPosition(refRMI),voisins);
    }
    if(!arene.estPersonnageFromRef(refCible)) refCible = 0;
    return refCible;
  }
  
}