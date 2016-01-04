package serveur.vuelement;

import java.awt.Color;
import java.awt.Point;
import java.io.Serializable;
import java.util.Random;

import serveur.element.Element;
import utilitaires.Calculs;

/**
 * Donnees dont le serveur a besoin sur un element : l'element lui-meme, sa 
 * position dans l'arene, sa reference...
 * Ces vues ne devraient pas etre utilisees dans le client pour le personnage, 
 * mais seulement dans le serveur et dans l'IHM. 
 */
// TODO parametriser? possible?
public class VueElement implements Serializable {
	
	private static final long serialVersionUID = 1750601856220885598L;

	/**
	 * Reference RMI.
	 */
	protected final int refRMI;

	/**
	 * L'element : son nom, son groupe, ses caracteristiques.
	 */
	protected Element element;
	
	/**
	 * Position dans l'arene.
	 */
	protected Point position;
	
	/**
	 * Couleur de l'element.
	 */
	protected Color color;
	
	/**
	 * Phrase dite par l'element.
	 */
	protected String phrase;
	
	/**
	 * Vrai si l'element est selectionne sur l'IHM.
	 */
	protected boolean selectionne = false;
	
	/**
	 * Vrai si l'element est en attente d'etre envoye sur l'arene.
	 */
	protected boolean enAttente;
	
	/**
	 * Cree un element pour le serveur.
	 * @param element element correspondant
	 * @param position position courante
	 * @param ref reference
	 * @param envoyeImm vrai si l'element doit etre envoye immediatement
	 */
	public VueElement(Element element, Point position, int ref, boolean envoyeImm) {
		this.element = element;
		this.position = position;
		this.refRMI = ref;
		
		Random r = new Random(ref);
		color = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255), 200);
		
		phrase = "";
		
		enAttente = !envoyeImm;
	}
	
	/**
	 * Envoie un element en attente, en jeu.
	 */
	public void envoyer() {
		enAttente = false;
	}

	public int getRefRMI() {
		return refRMI;
	}

	public Element getElement() {
		return element;
	}

	public Point getPosition() {
		return position;
	}

	public void setPosition(Point position) {
		this.position = Calculs.restreindrePositionArene(position);
	}
	
	public Color getColor() {
		return color;
	}
	
	public String getPhrase() {
		return phrase;
	}

	public void setPhrase(String phrase) {
		this.phrase = phrase;
	}
	
	public boolean isSelectionne() {
		return selectionne;
	}

	public void setSelectionne(boolean selectionne) {
		this.selectionne = selectionne;
	}

	public boolean isEnAttente() {
		return enAttente;
	}
}
