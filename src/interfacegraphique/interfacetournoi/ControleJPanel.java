package interfacegraphique.interfacetournoi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import interfacegraphique.IHM;
import interfacegraphique.IHMTournoi;

/**
 * Panel de controle de la partie : permet d'ejecter un element, de lancer la 
 * partie, d'ajouter une potion...
 *
 */
public class ControleJPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	/**
	 * Bouton permettant d'ejecter un element.
	 */
	private JButton ejecterButton;
	
	/**
	 * Bouton permettant d'afficher une vue detaillee d'un element selectionne.
	 */
	private JButton detaillerButton;
	
	/**
	 * Bouton permettant de lancer la partie.
	 */
	private JButton lancerPartieButton;
	
	/**
	 * Bouton permettant d'ouvrir la fenetre de creation de potion. 
	 */
	private JButton ajouterPotionButton;
	
	/**
	 * Bouton permettant d'envoyer une potion selectionnee.
	 */
	private JButton envoyerPotionButton;
	
	/**
	 * Bouton permettant d'acceder aux fonctionnalites admin de l'interface, 
	 * apres avoir tape le mot de passe.
	 */
	private JButton deverrouillerButton;

	/**
	 * IHM de tournoi, contient les methodes de communication avec le serveur.
	 */
	private final IHMTournoi ihmTournoi;

	/**
	 * Cree un panel de controle.
	 * @param ihm IHM de tournoi
	 */
	public ControleJPanel(IHMTournoi ihm) {
		this.ihmTournoi = ihm;	
		initComposants();
	}

	/**
	 * Initialise les composants du panel.
	 */
	private void initComposants() {
		this.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, IHM.grisFonce));

		this.setBackground(IHM.grisClair);
		this.setOpaque(true);
		
		ejecterButton = new JButton("Ejecter l'element");
		detaillerButton = new JButton("Vue detaillee");
		lancerPartieButton = new JButton("Lancer la partie");
		ajouterPotionButton = new JButton("Ajouter une potion");
		envoyerPotionButton = new JButton("Envoyer la potion");
		deverrouillerButton = new JButton("Deverouiller");	
		
		// listeners
		lancerPartieButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
        		ihmTournoi.lancerCompteARebours();	
            }
        });		
		
		ejecterButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
        		ihmTournoi.ejecteSelectionne();
            }
        });
		
		ejecterButton.setEnabled(false);
		
		detaillerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
        		ihmTournoi.detailleSelectionne(null);
            }
        });
		
		detaillerButton.setEnabled(false);
		
		ajouterPotionButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				ihmTournoi.afficherFenetrePotion();
			}			
		});
		
		envoyerPotionButton.addActionListener(new ActionListener() { 
			public void actionPerformed(ActionEvent e) { 
				ihmTournoi.envoyerPotionSelectionnee();
			}			
		});
		
		envoyerPotionButton.setEnabled(false);
		
		deverrouillerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				ihmTournoi.demanderMotDePasse();				
			}
		});
				
		this.add(detaillerButton);
		this.add(ejecterButton);
		this.add(lancerPartieButton);
		this.add(ajouterPotionButton);
		this.add(envoyerPotionButton);
		this.add(deverrouillerButton);
		
		verouiller();
		
		this.setVisible(true);
	}

	/**
	 * Verrouille le panneau de controle.
	 */
	public void verouiller() {
		detaillerButton.setVisible(false);
		ejecterButton.setVisible(false);
		lancerPartieButton.setVisible(false);
		ajouterPotionButton.setVisible(false);
		envoyerPotionButton.setVisible(false);
		
		deverrouillerButton.setVisible(true);		
	}

	/**
	 * Deverrouille le panneau de controle.
	 */
	public void deverouiller() {
		detaillerButton.setVisible(true);
		ejecterButton.setVisible(true);
		lancerPartieButton.setVisible(true);
		ajouterPotionButton.setVisible(true);
		envoyerPotionButton.setVisible(true);
		
		deverrouillerButton.setVisible(false);		
	}
	

	public JButton getEjecterButton() {
		return ejecterButton;
	}

	public JButton getLancerPartieButton() {
		return lancerPartieButton;
	}

	public JButton getDetaillerButton() {
		return detaillerButton;
	}

	public JButton getEnvoyerPotionButton() {
		return envoyerPotionButton;
	}


}
