package serveur.interaction;

import java.io.IOException;
import java.util.HashMap;

import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;
import lanceur.ErreurLancement;

public class LancePotionTrappeur {
	
	private static String usage = "USAGE : java " + LancePotionTrappeur.class.getName() + " [ port [ ipArene ] ]";

	public void LancePotion (int port, String ipArene, int refRMI) 
	{
		String nom = ">-<";
		
		// TODO remplacer la ligne suivante par votre numero de groupe
		String groupe = "G19" ; 
		
		// creation du logger
		LoggerProjet logger = null;
		try {
			logger = new LoggerProjet(true, "potion_"+nom+groupe);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
		
		// lancement de la potion
		try {
			IArene arene = (IArene) java.rmi.Naming.lookup(Constantes.nomRMI(ipArene, port, "Arene"));

			logger.info("lanceur", "Lancement de la potion trappeur sur le serveur...");
			
			// caracteristiques de la potion
			HashMap<Caracteristique, Integer> caractsPotion = new HashMap<Caracteristique, Integer>();
			caractsPotion.put(Caracteristique.VIE, -Constantes.POTION_TRAPPEUR_VIE);
			caractsPotion.put(Caracteristique.FORCE, -Constantes.POTION_TRAPPEUR_FORCE);
			caractsPotion.put(Caracteristique.INITIATIVE, 0);
			
			// ajout de la potion
			arene.ajoutePotionTrappeur(new Potion(nom, groupe, caractsPotion), refRMI);
			logger.info("lanceur", "Lancement de la potion trappeur reussi");
			
		} catch (Exception e) {
			logger.severe("lanceur", "Erreur lancement :\n" + e.getCause());
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
	}
}
