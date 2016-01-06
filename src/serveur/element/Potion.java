package serveur.element;

import java.util.HashMap;

/**
 * Une potion: un element donnant des bonus aux caracteristiques de celui qui
 * le ramasse.
 */
public class Potion extends Element {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Vrai si la potion a ete ramassee.
	 */
	private boolean ramassee;
	
	
	/**
	 * Vrai si la potion st une armure
	 */
	private boolean armure ;
	
	/**
	 * Constructeur d'une potion avec un nom, le groupe qui l'a envoyee et ses 
	 * caracteristiques (ajoutees lorsqu'un Personnage ramasse cette potion).
	 * @param nom nom de la potion
	 * @param groupe groupe d'etudiants de la potion
	 * @param caracts caracteristiques de la potion
	 */
	public Potion(String nom, String groupe, HashMap<Caracteristique, Integer> caracts,boolean estarmure) {
		super(nom, groupe, caracts);
		ramassee = false;
		armure = estarmure ;
	}
	
	/**
	 * Ramasser cette potion. 
	 */
	public void ramasser() {
		ramassee = true;
	}

	@Override
	public boolean estVivant() {
		return !ramassee;
	}
	
	public boolean getArmure(){
		return armure;
	}
}
