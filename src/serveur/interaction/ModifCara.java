package serveur.interaction;
import java.awt.Point;
import java.rmi.RemoteException;
import java.util.logging.Level;

import serveur.Arene;
import serveur.element.Caracteristique;
import serveur.element.Personnage;
import serveur.vuelement.VuePersonnage;
import utilitaires.Calculs;
import utilitaires.Constantes;


import serveur.vuelement.VuePersonnage;



public class ModifCara{

	private Arene arene;
	private VuePersonnage perso;
	private int modif;
	Caracteristique cara;
	public ModifCara(Arene arene, VuePersonnage perso,int modif, Caracteristique cara){
		this.arene = arene;
		this.perso = perso;
		this.modif = modif;
		this.cara = cara;
	}
	
	public void agir(){
		try{
			arene.ajouterCaractElement(perso, cara, modif);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
}
