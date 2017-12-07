package pfe.polytech.Vuzix_M100_entrepot.model;

/**
 * Cette classe correspond à un utilisateur des lunettes.
 *
 * Elle comprend son nom et le code barre qui lui ai associé
 */

public class Utilisateur {

    /** Nom de l'utilisateur*/
    private String nom;
    /** Code barre associé à l'utilisateur*/
    private String codeBarre;

    /**
     * Constructeur de la classe Utilisateur
     * @param nomUser Nom de l'utilisateur
     * @param CodeBarreUser Code barre associé à l'utilisateur
     */
    public Utilisateur( String nomUser, String CodeBarreUser)
    {
        nom = nomUser;
        codeBarre = CodeBarreUser;
    }


    /**
     * Vérifie dans la base de données si l'utilisateur existe via son code barre.
     * Si l'utilisateur existe, créer un objet utilisateur.
     * Retourne un booléen True si l'utilisateur existe, false sinon.
     * @param codeBarreLunette code barre vu par les lunettes
     * @return True si l'utilisateur existe, false sinon.
     */
    public static boolean verifieUtilisateur( String codeBarreLunette)
    {
        //Todo: Faire la verification via le serveur (Pour Vincent)
        //Todo: si existe => creer user sinon juste renvoyer false
        return false;
    }


    /**
     * Récupère le nom de l'utilisateur des lunettes
     * @return le nom de l'utilisateur des lunettes ( String)
     */
    public String getNom() {
        return nom;
    }

    /**
     * Récupère le code barre de l'utilisateur des lunettes
     * @return le code barre de l'utilisateur des lunettes ( String)
     */
    public String getCodeBarre() {
        return codeBarre;
    }
}
