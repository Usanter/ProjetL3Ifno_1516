package client;


import client.controle.Console;
import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Monstre;
import utilitaires.Calculs;
import utilitaires.Constantes;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.HashMap;


public class StrategieMonstre extends StrategiePersonnage {

    public StrategieMonstre(String ipArene, int port, String ipConsole,
                            int nbTours, Point position, LoggerProjet logger) {

    	super(logger);

        try {
            console = new Console(ipArene, port, ipConsole, this,
                    new Monstre(), nbTours, position, logger);
            logger.info("Lanceur", "Creation de la console reussie");

        } catch (Exception e) {
            logger.info("Personnage", "Erreur lors de la creation de la console : \n" + e.toString());
            e.printStackTrace();
        }
    }

    public void executeStrategie(HashMap<Integer, Point> voisins) throws RemoteException {
        IArene arene = console.getArene();
        int refRMI = 0;
        Point position = null;

        try {
            refRMI = console.getRefRMI();
            position = arene.getPosition(refRMI);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        int refCible = Calculs.chercheElementProche(position, voisins);
        
        while (refCible != 0) {
            if (!(arene.estMonstreFromRef(refCible) || arene.estPotionFromRef(refCible))) break;
            voisins.remove(refCible);
            refCible = Calculs.chercheElementProche(position, voisins);
        }

        if (refCible == 0) { // je n'ai pas de voisins, j'erre
            console.setPhrase("*En quete d'une proie*");
            arene.deplace(refRMI, 0);
        } else {
            int distPlusProche = Calculs.distanceChebyshev(position, arene.getPosition(refCible));

            if (distPlusProche <= Constantes.DISTANCE_MIN_INTERACTION) { // si suffisamment proches
                // duel
                console.setPhrase("Je fais un duel avec " + arene.nomFromRef(refCible));
                arene.lanceAttaque(refRMI, refCible);

            } else { // si voisins, mais plus eloignes
                // je vais vers le plus proche
                console.setPhrase("Je vais vers mon voisin " + arene.nomFromRef(refCible));
                arene.deplace(refRMI, refCible);
            }
        }
    }


}
