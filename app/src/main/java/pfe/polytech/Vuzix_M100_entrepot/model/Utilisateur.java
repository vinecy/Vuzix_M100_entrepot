package pfe.polytech.Vuzix_M100_entrepot.model;

import pfe.polytech.Vuzix_M100_entrepot.Connexionasync;

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
     * Constructeur de la classe Utilisateur par défaut
     */
    public Utilisateur( )
    {
        nom = null;
        codeBarre = null;
    }


    /**
     * Vérifie dans la base de données si l'utilisateur existe via son code barre.
     * Si l'utilisateur existe, créer un objet utilisateur.
     * Retourne un booléen True si l'utilisateur existe, false sinon.
     * @param codeBarreLunette code barre vu par les lunettes
     * @return True si l'utilisateur existe, false sinon.
     */
    public boolean verifieUtilisateur( String codeBarreLunette)
    {
        Connexionasync connexion = new Connexionasync();
        connexion.execute("htpp://bartholomeau.fr/identification.php?cb="+codeBarreLunette);
        if(connexion.getResult().equals("false"))
        {
            return false;
        }
        else
        {
            //position de la premiere virgule
            int index1 = connexion.getResult().indexOf(",");
            this.nom=connexion.getResult().substring(0,index1);
            this.codeBarre=connexion.getResult().substring(index1);
            return true;
        }
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
