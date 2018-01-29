package pfe.polytech.Vuzix_M100_entrepot;

import android.app.Application;

/**
 * Etats de l'application.
 * Cette classe contient l'état en cours.
 * Il s'agit d'un singleton.
 */

public class EtatSingleton {

    /**
     * Enumère les differentes états de l'application
     */
    public enum App_State{
        INIT,                   // Au démarrage de l'application
        SIGN_IN,                // Page de démarrage de l'applivation : invitation à l'authentification
        SCAN_USER,              // Ouverture de l'appareil de photo pour scanner le CB de l'utilisateur
        SEARCH_USER,            // Recherche de l'utilisateur d'après le code barre
        SEARCH_COMMAND,         // Page de chargement de la commande
        NAVIGATION1,            // Navigation vers un article de la commande
        SCAN_PRODUCT,           // Ouverture de l'appareil de photo pour scanner le CB du produit
        SEARCH_PRODUCT,         // Recherche de l'article via le code-barre
        QUANTITY_INPUT,         // Clavier numérique pour la saisie de la quantité
        NAVIGATION2,            // Navigation vers le dépot de la commande finie
        COMMAND_ENDED,          // Commande finie: choix entre déconnexion ou nouvelle commande
        SIGN_OUT                // Desauthentification
    }

    /** Objet singleton*/
    private static EtatSingleton etatObjet = null;
    /** Etat en cours*/
    private App_State etat;


    /**
     * Constructeur : initialisation à l'état de départ (INIT)
     */
    private EtatSingleton()
    {
        etat = App_State.INIT;
    }

    /**
     * Creation ou récupération de l'objet singleton
     * @return l'objet Singleton. Si l'objet n'existe pas, l'état est celui d'initialisation.
     */
    public final static EtatSingleton getSingleton() {
        if ( etatObjet == null)
        {
            etatObjet = new EtatSingleton();
        }
        return etatObjet;
    }

    /**
     * Change l'état de l'objet
     * @param nvx_etat le nouvelle état de l'objet (disponible dans l'énumération des états: App_state)
     * @return l'etat en cours
     */
    public App_State setEtat( EtatSingleton.App_State nvx_etat)
    {

        etatObjet.etat = nvx_etat;
        return etatObjet.etat;
    }

    /**
     * Acces à l'état courant
     * @return l'état courant
     */
    public App_State getEtat( )
    {
        return etatObjet.etat;
    }

}
