package serveur.interaction;

import java.util.HashMap;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.vuelement.VuePersonnage;

public class Clairvoyance extends Interaction<VuePersonnage>{

	public Clairvoyance(Arene arene, VuePersonnage attaquant, VuePersonnage defenseur) {
		super(arene, attaquant, defenseur);
	}

	public HashMap<Caracteristique,Integer> clair(){
		//Personnage att = attaquant.getElement();
		//Personnage def = defenseur.getElement();
		return defenseur.getElement().getCaracts();
		
	}

	@Override
	public void interagit() {}
}
