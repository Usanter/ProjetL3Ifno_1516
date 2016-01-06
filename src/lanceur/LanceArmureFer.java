package lanceur;

import java.io.IOException;
import java.util.HashMap;

import logger.LoggerProjet;
import serveur.IArene;
import serveur.element.Caracteristique;
import serveur.element.Potion;
import utilitaires.Calculs;
import utilitaires.Constantes;

public class LanceArmureFer {
	
	private static String usage = "USAGE : java " + LancePotion.class.getName() + " [ port [ ipArene ] ]";

	public static void main(String[] args) {
		String nom = "Armure de fer";
		
		// TODO remplacer la ligne suivante par votre numero de groupe
		String groupe = "G19" ; 
		
		// init des arguments
		int port = Constantes.PORT_DEFAUT;
		String ipArene = Constantes.IP_DEFAUT;
		
		if (args.length > 0) {
			if (args[0].equals("--help") || args[0].equals("-h")) {
				ErreurLancement.help(usage);
			}
			
			if (args.length > 2) {
				ErreurLancement.TROP_ARGS.erreur(usage);
			}
			
			try {
				port = Integer.parseInt(args[0]);
			} catch (NumberFormatException e) {
				ErreurLancement.PORT_NAN.erreur(usage);
			}
			
			if (args.length > 1) {
				ipArene = args[1];
			}
		}
		
		// creation du logger
		LoggerProjet logger = null;
		try {
			logger = new LoggerProjet(true, "Armure_"+nom+groupe);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
		
		// lancement de la potion
		try {
			IArene arene = (IArene) java.rmi.Naming.lookup(Constantes.nomRMI(ipArene, port, "Arene"));

			logger.info("lanceur", "Lancement de l'armure sur le serveur...");
			
			// caracteristiques de l'armure
			HashMap<Caracteristique, Integer> caractsPotion = new HashMap<Caracteristique, Integer>();
			caractsPotion.put(Caracteristique.ARMURE, Calculs.nombreAleatoire(15,25 ));
			caractsPotion.put(Caracteristique.VIE, 0);
			caractsPotion.put(Caracteristique.BLOCK, 0);
			caractsPotion.put(Caracteristique.COMPTEUR, 0);
			caractsPotion.put(Caracteristique.FORCE, 0);
			caractsPotion.put(Caracteristique.POUVOIR, 0);
			caractsPotion.put(Caracteristique.INITIATIVE, 0);
			// ajout de l'armure
			arene.ajoutePotion(new Potion(nom, groupe, caractsPotion,true));
			logger.info("lanceur", "Lancement de l'armure reussi");
			
		} catch (Exception e) {
			logger.severe("lanceur", "Erreur lancement :\n" + e.getCause());
			e.printStackTrace();
			System.exit(ErreurLancement.suivant);
		}
	}
}
