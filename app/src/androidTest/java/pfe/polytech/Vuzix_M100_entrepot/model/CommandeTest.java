package pfe.polytech.Vuzix_M100_entrepot.model;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by ju on 08/12/2017.
 */

public class CommandeTest {

    /**
     * Test : passage à l'article suivant
     * @throws Exception
     */
    @Test
    public void compareCodeBarreTest() throws Exception {
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
}
