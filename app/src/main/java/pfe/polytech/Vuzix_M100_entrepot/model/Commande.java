package pfe.polytech.Vuzix_M100_entrepot.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import pfe.polytech.Vuzix_M100_entrepot.Connexionasync;
import pfe.polytech.Vuzix_M100_entrepot.R;

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


    /** Clef du fichier JSON renvoyé par le serveur*/
    private static final String ID_CMD_JSON_KEY = "Idcommande";
    private static final String DEPOT_JSON_KEY = "Depot";
    private static final String ENTREPOT_JSON_KEY = "Entrepot";
    private static final String ID_ARTICLE ="Idarticle";


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
     * TODO: + verif que ya pas d'evenement pour cette commande
     */
    public static Commande chargerCommande( Utilisateur preparateurBdd) throws JSONException {
        Connexionasync connexion = new Connexionasync();
        connexion.execute("http://bartholomeau.fr/recevoir_commande.php?cb=" + preparateurBdd.getCodeBarre());
        try {
            Log.d("commande","message "+connexion.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        //Si une commande a été envoyé
        if (!connexion.getResult().substring(0, 1).equals("i")) {
            String ARTICLE_JSON_KEY = "Article";
            // Transforme le string en Json
            JSONObject jsonObj = new JSONObject(connexion.getResult());
            // Récupère les données
            String idCmd = jsonObj.getString(ID_CMD_JSON_KEY);
            String depot = jsonObj.getString(DEPOT_JSON_KEY);
            String ent = jsonObj.getString(ENTREPOT_JSON_KEY);

            //Creation de la liste d'article
            List<Article> list_article = new ArrayList<>();
            boolean keyExiste = true;
            int nb = 1;
            // Clef JSON du premier article
            String nbArticle = ARTICLE_JSON_KEY + Integer.toString(nb);
            // Parcours tout les articles tant qu'il y en a
            while (keyExiste) {
                // Récupère le JSON d'un article
                JSONObject jsonArticle = new JSONObject(jsonObj.getString(nbArticle));
                // Creer l'article correspondant
                Article article = new Article(Integer.parseInt( jsonArticle.getString(ID_ARTICLE)), jsonArticle.getString("nom"), jsonArticle.getString("nbcpdebarre"), jsonArticle.getString("allee"), jsonArticle.getString("etagere"), jsonArticle.getString("emplacement"), Integer.parseInt(jsonArticle.getString("quantite")));
                // Ajoute cette article à la liste
                list_article.add(article);
                // Passe à l'article suivant
                nb++;
                nbArticle = ARTICLE_JSON_KEY + Integer.toString(nb);
                // Vérifie si cette article existe (false sinon)
                keyExiste = jsonObj.has(nbArticle);
            }
            // Creation de la commande
            Commande cmd = new Commande(Integer.parseInt(idCmd), list_article, depot, ent, preparateurBdd);
            return cmd;
        } else {
            return null;
        }
    }

    /**
     * Vérifie si l'article scanné par les lunette correspond à l'article courant.
     * @param codeBarre Code barre de l'article scanné par les lunettes
     * @return True s'il n'y a pas de problème, l'article est conforme ,
     *         False si le code barre ne correspond pas à l'article en cours de la commande,
     */
    public boolean checkArticle( String codeBarre)
    {
        System.out.println("TEST DANS CHECK ARTICLE!!!!");
        return articleList.get( ptrArticleList).compareCodeBarre( codeBarre);
    }


    /**
     * Vérifie si la quantité demandé dans la commande pour l'article courant est la même que celle indiqué par l'utilisateur.
     * @param quantite Quantité de l'artice pris par l'utilisateur
     * @return True s'il n'y a pas de problème, l'a quantité de l'article courant est correcte,
     *         False si la quantité prise ne corespond pas à celle de l'article en cours de la commande
     */
    public boolean checkQuantite( int quantite)
    {
        boolean quantiteOk = articleList.get( ptrArticleList).compareQuantite( quantite);
        if(! quantiteOk)
        {
            //this.erreurCommande( "La quantité du panier insufisante pour l'atricle "+ articleList.get( ptrArticleList).getNom(), articleList.get( ptrArticleList).get);
        }
        return quantiteOk;
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
        Connexionasync connexion = new Connexionasync();
        String cb = this.getPreparateur().getCodeBarre();
        int idCommande = this.getId();
        String url="http://bartholomeau.fr/envoieCommandeEnCours.php?codeBarre="+cb+"&idCommande="+idCommande;
        connexion.execute(url);

    }

    /**
     * Signal au serveur que la commande est terminé.
     * TODO: Zied => envoyé et mettre a jour dans bdd
     */
    public void FinCommande()
    {
        int idCommande = this.getId();
        Connexionasync connexion = new Connexionasync();
        String cb = this.preparateur.getCodeBarre();
        String url="http://bartholomeau.fr/findCommande.php?cb="+cb+"&idCommande="+idCommande;
        connexion.execute(url);
    }

    /**
     * Envoie au serveur l'evenement concernant la commande et l'article en cours.
     * @param typeEvenement Message explicitant l'erreur survenu dans la commande
     * TODO Zied => ajouter dans bdd
     */
    public void erreurCommande( String typeEvenement ,String idArticle)
    {
        Connexionasync connexion = new Connexionasync();
        int codeBarrePrep = this.getPreparateur().getIdUser();
        String identifiant = this.getPreparateur().getCodeBarre();
        int idCommande= this.getId();
        String url=("http://bartholomeau.fr/evenement.php?identifiant="+identifiant+"&codeBarrePrep="+codeBarrePrep+"&idCommande="+idCommande+"&idArticle="+idArticle+"&typeEvenement="+typeEvenement);
        connexion.execute(url);
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
