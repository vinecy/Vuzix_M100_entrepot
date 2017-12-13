package pfe.polytech.Vuzix_M100_entrepot.model;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Classe permettant de réaliser des tests sur les méthodes de la classe Commande
 */

public class CommandeTest {

    /**
     * Test : passage à l'article suivant
     * @throws Exception
     */
    @Test
    public void articleSuivantTest() throws Exception {
        //Liste d'articles
        Article test1 = new Article( "Machine 1","123456789", "A","5","102",2);
        Article test2 = new Article( "Machine 2","15263", "A","5","103",1);
        Article test3 = new Article( "Machine 3","15486532184", "A","5","105",5);
        List< Article > list_article = new ArrayList< >();
        list_article.add( test1);
        list_article.add( test2);
        list_article.add( test3);

        // Utilisateur
        Utilisateur user = new Utilisateur( "Nom1","1111111111111");
        //Commande
        Commande cmd = new Commande(1,list_article,"depot","entrepot A",user);

        // Test :
        // Récupére le premier article
        assertEquals( test1.getCodeBarre() , cmd.getArticleCourrant().getCodeBarre());
        // Passe au second article
        assertEquals( test2.getCodeBarre() , cmd.ArticleSuivant().getCodeBarre());
        // Passe au troisième article
        assertEquals( test3.getCodeBarre() , cmd.ArticleSuivant().getCodeBarre());
        // Il n'y a plus d'articles, doit retourner null
        assertEquals( null , cmd.ArticleSuivant());
    }

    /**
     * Test sur la fonction checkArticle
     */
    @Test
    public void checkArticleTest(){
        //Liste d'articles
        Article test1 = new Article( "Machine 1","123456789", "A","5","102",2);
        Article test2 = new Article( "Machine 2","15263", "A","5","103",1);
        Article test3 = new Article( "Machine 3","15486532184", "A","5","105",5);
        List< Article > list_article = new ArrayList< >();
        list_article.add( test1);
        list_article.add( test2);
        list_article.add( test3);

        // Utilisateur
        Utilisateur user = new Utilisateur( "Nom1","1111111111111");
        //Commande
        Commande cmd = new Commande(1,list_article,"depot","entrepot A",user);

        //Test sur le premier article:
        assertEquals( true , cmd.checkArticle( "123456789"));
        assertEquals( false , cmd.checkArticle( "1234"));
        cmd.ArticleSuivant();
        // Test sur le second
        assertEquals( true , cmd.checkArticle( "15263"));
        assertEquals( false , cmd.checkArticle( ""));
    }


    /**
     * Test sur la fonction checkQuantite
     */
    @Test
    public void checkQuantiteTest(){
        //Liste d'articles
        Article test1 = new Article( "Machine 1","123456789", "A","5","102",2);
        Article test2 = new Article( "Machine 2","15263", "A","5","103",1);
        Article test3 = new Article( "Machine 3","15486532184", "A","5","105",5);
        List< Article > list_article = new ArrayList< >();
        list_article.add( test1);
        list_article.add( test2);
        list_article.add( test3);

        // Utilisateur
        Utilisateur user = new Utilisateur( "Nom1","1111111111111");
        //Commande
        Commande cmd = new Commande(1,list_article,"depot","entrepot A",user);

        //Test sur le premier article:
        assertEquals( true , cmd.checkQuantite( 2));
        assertEquals( false , cmd.checkQuantite( 0));
        cmd.ArticleSuivant();
        // Test sur le second
        assertEquals( true , cmd.checkQuantite( 1));
        assertEquals( false , cmd.checkQuantite( 5));
    }


    @Test
    public void passJsonCmd() throws JSONException {
        String json = "{\"Idcommande\":0," +
                "\"Article1\":{\"Idarticle\":\"1\",\"nom\":\"fromage blanc Laitiere\",\"nbcpdebarre\":\"2154632156231\",\"allee\":\"A\",\"etagere\":\"5\",\"emplacement\":\"123\",\"quantite\":\"2\"}," +
                "\"Article2\":{\"Idarticle\":\"1\",\"nom\":\"fromage blanc Laitiere\",\"nbcpdebarre\":\"2154632156231\",\"allee\":\"A\",\"etagere\":\"5\",\"emplacement\":\"123\",\"quantite\":\"2\"}," +
                "\"Depot\":\"Entrepot X _ etagere B\"," +
                "\"Entrepot\":\"FROID\"}";

        final String ID_CMD_JSON_KEY = "Idcommande"; //TODO: deplacer la fonction et la mettre static et private
        final String DEPOT_JSON_KEY = "Depot";
        final String ENTREPOT_JSON_KEY = "Entrepot";
        String ARTICLE_JSON_KEY = "Article";

        // Transforme le string en Json
        JSONObject jsonObj = new JSONObject(json.toString());

        // Récupère les données
        String idCmd = jsonObj.getString( ID_CMD_JSON_KEY);
        String depot = jsonObj.getString( DEPOT_JSON_KEY);
        String ent = jsonObj.getString( ENTREPOT_JSON_KEY);

        //Creation de la liste d'article
        int maxArticle = 2;     //TODO: recupérer depuis le JSON
        List< Article > list_article = new ArrayList< >();
        for( int nb = 1; nb <= maxArticle ; nb ++)
        {
            String nbArticle = ARTICLE_JSON_KEY + Integer.toString(nb);
            JSONObject jsonArticle = new JSONObject( jsonObj.getString(nbArticle));
            Article article = new Article( jsonArticle.getString("nom"), jsonArticle.getString("nbcpdebarre"), jsonArticle.getString("allee"), jsonArticle.getString("etagere"), jsonArticle.getString("emplacement"), Integer.parseInt( jsonArticle.getString("quantite")) );
            list_article.add( article);
           // System.out.println("ARTICLE "+ jsonArticle);
           System.out.println("--------------------------------------");
           // System.out.println("Nom : " + article.getNom() + " cb : " + article.getCodeBarre() + " allee : " + article.getAllee() + " etagere : "+ article.getEtagere() + " emp : " + article.getEmplacementEtagere() + " qtite : " +article.getQuantiteDemande());

        }

        // Creation de la commande
        //TODO: remplacer dans la fonction charger commande le code barre par l'objet utilisateur!!!!!
        Commande cmd = new Commande( Integer.parseInt( idCmd),list_article,depot,ent,null);
        System.out.println(" CMD ::::: id= " + cmd.getId() + " depot " + cmd.getDepot()  + " user  " + cmd.getPreparateur() + " ent " + cmd.getEntrepot());

    }

}
