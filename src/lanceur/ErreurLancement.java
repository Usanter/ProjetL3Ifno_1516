package lanceur;

/**
 * Enumeration des erreurs de saisies au lancement d'une des classes de lanceur.
 *
 */
public enum ErreurLancement {
	TROP_ARGS		(1, "Trop d'arguments !"),
	PORT_NAN		(2, "Le premier argument doit etre une nombre entre 0 et 65535 !"),
	INCORRECT_IP	(3, "Adresse IP de l'arene incorrect !"),
	TTL_INCORRECT	(4, "Le TTL doit etre un nombre positif");
	
	public static int suivant = 5;

	/**
	 * Code de l'erreur.
	 */
	public int code;
	
	/**
	 * Description de l'erreur.
	 */
	public String str;
	
	/**
	 * Cree une erreur de lancement.
	 * @param code code 
	 * @param str description
	 */
	private ErreurLancement(int code, String str) {
		this.code = code;
		this.str = str;
	}
	
	/**
	 * Affiche l'erreur sur System.err.
	 * @param usage utilisation correcte
	 */
	public void erreur(String usage) {
		System.err.println("\tErreur "+code+" : "+str);
		System.err.println("\t"+usage);
		System.exit(code);
	}
	
	/**
	 * Affiche l'aide du programme.
	 * @param usage utilisation correcte
	 */
	public static void help(String usage) {
		System.out.println(usage);
		System.out.println();
		System.out.println("Ce programme fait partie d'une suite de logiciels"
						+ "servant pour le projet de programmation de L3 de la"
						+ "licence d'informatique de l'universite Paul Sabatier.");
		System.out.println();
		System.out.println("Voici la liste des codes de retour valable pour toute la suite logiciel :");
		System.out.println("\tCode 0 : Aucun probleme d'execution");
		for (ErreurLancement e : values()) {
			System.out.println("\tCode "+e.code+" : "+e.str);
		}
		System.out.println("\tCode "+suivant+" : Tout autre probleme");
		System.exit(0);
	}
}
