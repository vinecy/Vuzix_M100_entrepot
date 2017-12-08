package pfe.polytech.Vuzix_M100_entrepot.model;

import org.json.JSONArray;

import java.util.List;

/**
 * Cette classe correspond à une commande.
 * Elle comprend une liste d'objets Articles, l'entrepot où se déroule la commande, l'endroit de dépot,
 * l'identité du préparateur de commande ainsi que l'identifiant de cette commande.
 *
 */

public class Commande {

    /** Identifiant de la commande dans la base de données du serveur*/
    private int id;
    /** Liste des articles de la commande*/
    private List< Article> articleList;
    /** Endroit du dépot de la commande*/
    private String depot;
    /** Entrepot où la commande est effectué*/
    private String entrepot;
    /** Identification du préparateur associé à cette commande*/
    private Utilisateur preparateur;


    /** Pointeur sur la liste d'articles. Permet d'obtenir le prochain article*/
    private int ptrArticleList;


    /**
     * Constructeur d'une commande.
     * Le pointeur sur la lsite d'article est initilisé à zéros.
     * @param idBdd Identifiant de la commande dans la base de données du serveur
     * @param articleListBdd Liste des articles de la commande
     * @param depotBdd Endroit du dépot de la commande
     * @param entrepotBdd Entrepot où la commande est effectué
     * @param preparateurBdd Identification du préparateur associé à cette commande
     */
    public Commande( int idBdd, List< Article> articleListBdd, String depotBdd, String entrepotBdd, Utilisateur preparateurBdd)
    {
        id = idBdd;
        articleList = articleListBdd;
        depot = depotBdd;
        entrepot = entrepotBdd;
        preparateur = preparateurBdd;
        ptrArticleList = 0;
    }


    /**
     * Récupère la commande (JSON) depuis le serveur.
     * @param preparateurBdd l'utilisateur des lunettes
     * @return La commande sous un format JSON
     * TODO: (Pour Vincent) : verifier si objet Json = OK, puis ajouter la recupération de la commande via le serveur
     * TODO: + verif que ya pas d'evenement pour cette commande
     */
    public JSONArray chargerCommande( Utilisateur preparateurBdd)
    {
        return null;
    }

    /**
     * Vérifie si l'article scanné par les lunette correspond à l'article courant.
     * @param codeBarre Code barre de l'article scanné par les lunettes
     * @return True s'il n'y a pas de problème, l'article est conforme ,
     *         False si le code barre ne correspond pas à l'article en cours de la commande,
     */
    public boolean checkArticle( String codeBarre)
    {
        return articleList.get( ptrArticleList).compareCodeBarre( codeBarre);
    }


    /**
     * Vérifie si la quantité demandé dans la commande pour l'article courant est la même que celle indiqué par l'utilisateur.
     * @param quantite Quantité de l'artice pris par l'utilisateur
     * @return True s'il n'y a pas de problème, l'a quantité de l'article courant est correcte
     *         False si la quantité prise ne corespond pas à celle de l'article en cours de la commande,
     */
    public boolean checkQuantite( int quantite)
    {
        return articleList.get( ptrArticleList).compareQuantite( quantite);
    }

    /**
     * Passe à l'article suivant dans la commande. Retourne l'article suivant.
     * @return L'article suivant de la commande. S'il n'y a plus d'article, la fonction renvoit null.
     */
    public Article ArticleSuivant()
    {
        ptrArticleList ++;
        // S'il reste des articles dans la commande
        if(  ptrArticleList < articleList.size()) {
            return articleList.get(ptrArticleList);

        }
        //Sinon, s'il n'y plus d'article à ajouter au "panier"
        else {
            return null;
        }
    }



    /**
     * Envoie au serveur l'identité (via le code barre) du préparateur associé à cette commande.
     * TODO: (Pour Zied) => envoyé au serveur le prep associé a la cmd + stocker dans bdd
     */
    public void envoieCommandeEnCours()
    {

    }

    /**
     * Signal au serveur que la commande est terminé.
     * TODO: Zied => envoyé et mettre a jour dans bdd
     */
    public void FinCommande()
    {

    }

    /**
     * Envoie au serveur l'evenement concernant la commande et l'article en cours.
     * @param erreur Message explicitant l'erreur survenu dans la commande
     * TODO Zied => ajouter dans bdd
     */
    public void erreurCommande( String erreur)
    {

    }


    /**
     * Récupére l'identifiant de la commande
     * @return l'identifiant de la commande  (int)
     */
    public int getId() {
        return id;
    }

    /**
     * Récupére la liste des articles de la commande
     * @return la liste des articles de la commande
     */
    public List<Article> getArticleList() {
        return articleList;
    }

    /**
     * Récupére l'article en cours dans la liste d'articles de la commande
     * @return l'article en cours dans la liste d'articles de la commande
     */
    public Article getArticleCourrant() {
        return articleList.get(ptrArticleList);
    }

    /**
     * Récupére l'endroit du dépot de la commande
     * @return l'endroit du dépot de la commande (String)
     */
    public String getDepot() {
        return depot;
    }

    /**
     * Récupére l'entrepot associé à la commande
     * @return l'entrepot associé à la commande (String)
     */
    public String getEntrepot() {
        return entrepot;
    }

    /**
     * Récupére le préparateur associé à la commande
     * @return le préparateur associé à la commande (String)
     */
    public Utilisateur getPreparateur() {
        return preparateur;
    }


    /**
     * Récupére le pointeur sur la liste des articles
     * @return le pointeur sur la liste des articles (int)
     */
    public int getPtrArticleList() {
        return ptrArticleList;
    }


}
