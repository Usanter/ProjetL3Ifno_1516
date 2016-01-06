package serveur.vuelement;

import java.awt.Point;

import serveur.element.Potion;

/**
 * Donnees que le serveur doit conserver sur chacun de ces clients potions.
 */
public class VuePotion extends VueElement implements Comparable<VuePotion> {
	
	private static final long serialVersionUID = 4227900415029065269L;
	
	private boolean estArmure;
	private boolean estVie;
	private boolean estArme;
	/**
	 * Cree une vue d'une potion personnage.
	 * @param potion potion correspondante
	 * @param position position courante
	 * @param ref reference RMI
	 * @param envoyeImm vrai si l'element doit etre envoye immediatement
	 */
	public VuePotion(Potion potion, Point position, int ref, boolean envoyeImm) {
		super(potion, position, ref, envoyeImm);
		estArmure = potion.getArmure();
		estVie = potion.getLife();
		estArme = potion.getWeapon();
	}

	@Override
	public int compareTo(VuePotion vp2) {
		return vp2.getRefRMI() - this.getRefRMI();
	}
	
	public boolean getEstArmure(){
		return estArmure;
	}
	
	public boolean getEstVie(){
		return estVie;
	}
	
	public boolean getEstArme(){
		return estArme;
	}
}
