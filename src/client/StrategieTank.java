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
public class StrategieTank extends StrategiePersonnage{
<<<<<<< HEAD
   
    public StrategieTank (String ipArene, int port, String ipConsole,
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
      /* 
        //on augmente la jauge de pouvoir si elle est inferieure au max
        if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) < Constantes.POUVOIR_MAX_TANK )
            arene.modifCara(refRMI, 1 , Caracteristique.POUVOIR);
        // si la jauge de pouvoir est au max, on la met a 0 et on augmente la force de VALEUR_POUVOIR_BARBARE
        if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) >= Constantes.POUVOIR_MAX_TANK
                && console.getPersonnage().getCaract(Caracteristique.BLOCK) < Constantes.ARMURE_MAX_TANK){
            arene.modifCara(refRMI, 1 , Caracteristique.BLOCK);
            arene.modifCara(refRMI, -Constantes.POUVOIR_MAX_TANK, Caracteristique.POUVOIR);
        } */
       
        if (voisins.isEmpty()) { // je n'ai pas de voisins, j'erre
            console.setPhrase("J'erre...");
            arene.deplace(refRMI, 0); 
        }  
        else {
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
                    console.setPhrase("Je vais vers " + elemPlusProche.getNom());
                    arene.deplace(refRMI, refCible);
                }
                // sinon c'est un personnage
                else{
                    // on va toujours vers lui, on est un tank quand meme
                        console.setPhrase(elemPlusProche.getNom()+" va sentir ma colere ...");
                        arene.deplace(refRMI, refCible);
                }
               
               
            }
        }
    }
}
=======
	
	public StrategieTank (String ipArene, int port, String ipConsole, 
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
		if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) < Constantes.POUVOIR_MAX_TANK )
			arene.modifCara(refRMI, 1 , Caracteristique.POUVOIR);
		// si la jauge de pouvoir est au max, on la met a 0 et on augmente la force de VALEUR_POUVOIR_BARBARE
		if(console.getPersonnage().getCaract(Caracteristique.POUVOIR) >= Constantes.POUVOIR_MAX_TANK 
				&& console.getPersonnage().getCaract(Caracteristique.BLOCK) < Constantes.ARMURE_MAX_TANK){
			arene.modifCara(refRMI, 1 , Caracteristique.BLOCK);
			arene.modifCara(refRMI, -Constantes.POUVOIR_MAX_TANK, Caracteristique.POUVOIR);
		}
		
		if (voisins.isEmpty()) { // je n'ai pas de voisins, j'erre
			if (console.getPersonnage().getCaract(Caracteristique.VIE) < 50)
			{
				Point position1 = arene.getPosition(refRMI); 
				if(arene.TestSurSpawn(refRMI , position1) )
				{
					console.setPhrase("Je suis dessus le spawn ");
				}
				else
				{
				console.setPhrase("Je me déplace vers le spwan " +position1.x + "  " + position1.y);
				arene.deplaceSpawn(refRMI, position); 
				}
			}
			else {
			console.setPhrase("J'erre...");
			arene.deplace(refRMI, 0); }
		}	
		else {
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
					console.setPhrase("Je vais vers " + elemPlusProche.getNom());
					arene.deplace(refRMI, refCible);
				}
				// sinon c'est un personnage
				else{
					// on va toujours vers lui, on est un tank quand meme
						console.setPhrase(elemPlusProche.getNom()+" va sentir ma colere ...");
						arene.deplace(refRMI, refCible);
				}
				
				
			}
		}
	}

	
}
>>>>>>> dfcb0a2d5610ec99dfcb732126e2f63e1edfa083
