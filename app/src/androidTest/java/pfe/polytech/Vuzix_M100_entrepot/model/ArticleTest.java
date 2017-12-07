package pfe.polytech.Vuzix_M100_entrepot.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Classe permattant de tester les fonctions de Article
 */

public class ArticleTest {

    @Test
    public void compareCodeBarreTest() throws Exception {
        Article test = new Article( "Machine","123456789", "A","5","102",2);
        assertEquals( true , test.compareCodeBarre( "123456789"));
        assertEquals( false , test.compareCodeBarre( "1234567891"));
    }

    @Test
    public void compareQuantiteTest() throws Exception {
        Article test = new Article( "Machine","123456789", "A","5","102",2);
        assertEquals( true , test.compareQuantite( 2));
        assertEquals( false , test.compareQuantite( 1));
    }
}
