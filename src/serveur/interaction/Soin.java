package serveur.interaction;

import java.rmi.RemoteException;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.vuelement.VuePersonnage;

public class Soin extends Interaction<VuePersonnage> {
	
	public Soin(Arene arene, VuePersonnage attaquant, VuePersonnage defenseur) {
		super(arene, attaquant, defenseur);
	}
	
	public void interagit(){
		try {			
			arene.incrementeCaractElement(defenseur, Caracteristique.VIE, 2);		
		} catch (RemoteException e) {
			logs(Level.INFO, "\nErreur lors d'un soin : " + e.toString());
		}
	}
}
