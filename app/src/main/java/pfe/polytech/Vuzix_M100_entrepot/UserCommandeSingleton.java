package pfe.polytech.Vuzix_M100_entrepot;

import pfe.polytech.Vuzix_M100_entrepot.model.Commande;
import pfe.polytech.Vuzix_M100_entrepot.model.Utilisateur;

/**
 * Utilisateur et commande en cours.
 * Cette classe contient l'utilisateur actuel de l'appication ainsi que la commande en cours.
 * Il s'agit d'un singleton.
 */

public class UserCommandeSingleton {

    /** Utilisateur en cours*/
    private Utilisateur user;
    /** Commande en cours*/
    private Commande cmd;
    /** Objet de la classe singleton*/
    private static UserCommandeSingleton instance = null;

    /**
     * Constructeur : initialisation de l'utilisateur et de la commande
     * Les objets initialisés sont vides.
     */
    private UserCommandeSingleton()
    {
        user = new Utilisateur("0", null, null);
        cmd = new Commande( 0, null, null, null, null);
    }

    /**
     * Creation ou récupération de l'objet singleton
     * Attention : les objets créer dans cette classe sont nulle!
     * @return l'objet Singleton. Si l'objet n'existe pas la commande et l'utilisateur sont vides.
     */
    public final static UserCommandeSingleton getSingleton() {
        if ( instance == null)
        {
            instance = new UserCommandeSingleton();
        }
        return instance;
    }

    /**
     * Change la commande
     * @param newCmd la nouvelle commande en cours
     * @return la nouvelle commande
     */
    public Commande setCommande( Commande newCmd)
    {
        instance.cmd = newCmd;
        return instance.cmd;
    }

    /**
     * Accès à la commande en cours
     * @return la commande en cours
     */
    public Commande getCommande()
    {
        return instance.cmd;
    }

    /**
     * Change l'utilisateur
     * @param newUser le nouvel utilisateur
     * @return le nouvel utilisateur
     */
    public Utilisateur setUtilisateur( Utilisateur newUser)
    {

        instance.user = newUser;
        return instance.user;
    }

    /**
     * Accès à l'utilisateur en cours
     * @return l'utilisateur en cours
     */
    public Utilisateur getUtilisateur()
    {
        return instance.user;
    }
}
