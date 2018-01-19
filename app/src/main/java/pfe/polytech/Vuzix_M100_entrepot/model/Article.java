package pfe.polytech.Vuzix_M100_entrepot.model;

/**
 * Cette classe représente les articles d'une commande.
 *
 * Un article possède un nom, un code barre, les informations de localisation
 * et les quantités demandé dans la commande et prise par l'utilisateur.
 */

public class Article {

    /** Nom de l'article*/
    private String nom;
    /** Code barre de l'article*/
    private String codeBarre;
    /** Emplacement du produit : Allée de l'entrepôt*/
    private String allee;
    /** Emplacement du produit : Etagère de l'allée*/
    private String etagere;
    /** Emplacement du produit : Emplacement sur l'étagère*/
    private String emplacementEtagere;
    /** Quantité souhaité par le client et demandé dans une commande définis*/
    private int quantiteDemande;
    /** Quantité récupérer par le préparateur de commande lors de la réalisation de celle-ci*/
    private int quantitePrise;
    /** identifiant de l'article (base de données)*/
    private int idArticle;


    /**
     * Constructeur de l'objet article.
     * La quantité prise par un utilisateur est initialisé à zéros lors de la création.
     * @param id Identifiant de l'article
     * @param nomCmd Nom de l'article
     * @param codeBarreCmd Code barre de l'article
     * @param alleeCmd Emplacement du produit : Allée de l'entrepôt
     * @param etagereCmd Emplacement du produit : Etagère de l'allée
     * @param emplacementEtagereCmd Emplacement du produit : Emplacement sur l'étagère
     * @param quantiteDemandeCmd Quantité souhaité par le client et demandé dans une commande définis
     */
    public Article( int id, String nomCmd, String codeBarreCmd, String alleeCmd, String etagereCmd, String emplacementEtagereCmd, int quantiteDemandeCmd)
    {
        idArticle = id;
        nom = nomCmd;
        codeBarre = codeBarreCmd;
        allee = alleeCmd;
        etagere = etagereCmd;
        emplacementEtagere = emplacementEtagereCmd;
        quantiteDemande = quantiteDemandeCmd;
        quantitePrise = 0;
    }


    /**
     * Compare le code barre de l'article avec celui récupéré par les lunettes.
     * Renvoit un booléen : True si le code barre lut par les lunettes est celui de l'article, faux sinon.
     * @param CodeBarreCamera Code barre lut par les lunettes
     * @return True si le code barre lut par les lunettes est celui de l'article, faux sinon
     */
    public boolean compareCodeBarre( String CodeBarreCamera)
    {
        return codeBarre.equals( CodeBarreCamera);
    }


    /**
     * Compare la quantité demandé par la commande pour cette article avec la quantité donné par le
     * préparateur de commande.
     * Renvoit un booléen : True si la quantité prise est la même que celle demandé dans la commande, faux sinon.
     * @param quantiteUser Quantité que l'utilisateur a mit dans son "panier" pour cette article.
     * @return True si la quantité prise est la même que celle demandé dans la commande, faux sinon.
     */
    public boolean compareQuantite( int quantiteUser)
    {
        quantitePrise = quantiteUser;
        return  quantiteDemande == quantitePrise;
    }


    /**
     * Récupère le nom de l'article
     * @return le nom de l'article (String)
     */
    public String getNom() {
        return nom;
    }

    /**
     * Récupère le code barre de l'article
     * @return le code barre de l'article (String)
     */
    public String getCodeBarre() {
        return codeBarre;
    }

    /**
     * Récupère l'allée de l'article
     * @return l'allée de l'article (String)
     */
    public String getAllee() {
        return allee;
    }

    /**
     * Récupère l'étagére de l'allée de l'article
     * @return  l'étagére de l'allée de l'article (String)
     */
    public String getEtagere() {
        return etagere;
    }

    /**
     * Récupère l'emplacement sur l'étagére de l'allée de l'article
     * @return  l'emplacement sur l'étagére de l'allée de l'article (String)
     */
    public String getEmplacementEtagere() {
        return emplacementEtagere;
    }

    /**
     * Récupère la quantité souhaité de l'article dans la commande
     * @return la quantité souhaité de l'article (Int)
     */
    public int getQuantiteDemande() {
        return quantiteDemande;
    }

    /**
     * Récupère la quantité prise de l'article pendant la réalisation de la commande
     * @return la quantité prise de l'article disponible dans le "panier" (Int)
     */
    public int getQuantitePrise() {
        return quantitePrise;
    }

    @Override
    public String toString()
    {
        return ("Article " + nom + " codebarre = "+ codeBarre);
    }


    /**
     * Récupère l'identifiant de l'article
     * @return l'identifiant de l'article (int)
     */
    public int getIdArticle() {
        return idArticle;
    }

}
