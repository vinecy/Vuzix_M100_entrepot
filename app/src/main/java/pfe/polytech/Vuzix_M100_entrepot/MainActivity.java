

package pfe.polytech.Vuzix_M100_entrepot;
import com.google.zxing.*;

import android.app.Activity;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

//import com.vuzix.speech.VoiceControl;

import org.json.JSONException;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import pfe.polytech.Vuzix_M100_entrepot.model.Article;
import pfe.polytech.Vuzix_M100_entrepot.model.Commande;
import pfe.polytech.Vuzix_M100_entrepot.model.Utilisateur;

/**
 * Enumère les differentes états de l'application
 */
enum App_State{
    INIT,                   // Au démarrage de l'application
    SIGN_IN,                // Page de démarrage de l'applivation : invit à l'authentification
    SCAN_USER,              // Ouverture de l'appareil de photo pour scanner le CB de l'utilisateur
    SEARCH_USER,            // Recherche de l'utilisateur d'après le code barre
    SEARCH_COMMAND,         // Page de chargement de la commande
    NAVIGATION1,            // Boussole vers l'article
    SCAN_PRODUCT,           // Ouverture de l'appareil de photo pour scanner le CB du produit
    SEARCH_PRODUCT,         // Recherche de l'article via le code-barre
    QUANTITY_INPUT,         // Clavier numérique pour la saisie de la quantité
    NAVIGATION2,            // Boussole vers le dépot
    COMMAND_ENDED,          // Soummision de la commande terminé + invit DESAUTH ou AGAIN
    SIGN_OUT                // Desauthentification
}

/**
 * Classe lançant le code de l'application
 */
public class MainActivity extends Activity implements ZXingScannerView.ResultHandler
{
    // ATTRIBUTS
    // Elements du MODELE
    private Utilisateur user;
    private Commande commande;

    // Elements de la VUE
    private ZXingScannerView zXingScannerView;      // IHM pour le scan du code-barre

    // Elements du CONTROLEUR
    private App_State app_state = App_State.INIT;   // Etat de l'application
    private String codeBarre_scanned = "";          // Code barre scanné
    private TextView textview_ptr;

    /**
     * Fonction au lancement de l'application. Au lancement, on doit s'identifier en scannant le
     * badge
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeState(App_State.SIGN_IN);                             // Au demarrage, on est sur la page d'authentification
    }

    /**
     * Gestionnaire de resultat de l'application. Appeler lors le scan a trouvé un code barre.
     * badge
     */
    @Override
    public void handleResult(Result result) {
        zXingScannerView.resumeCameraPreview(this);
        zXingScannerView.stopCamera();
        codeBarre_scanned = result.getText();
        Toast.makeText(getApplicationContext(),"Code Barre : " + codeBarre_scanned,Toast.LENGTH_SHORT).show();
        switch (app_state) {
            case SCAN_USER:
                changeState(App_State.SEARCH_USER);
                break;
            case SCAN_PRODUCT:
                changeState(App_State.SEARCH_PRODUCT);
                break;
        }
    }

    /**
     * Fonction qui permet de changer et effectue les traitements nécessaires correspondant
     * @param a nouvelle état de l'application
     */
    public void changeState(App_State a){
        app_state = a;
        switch (a) {
            case INIT:
                codeBarre_scanned = "";
            case SIGN_IN:
                setContentView(R.layout.activity_main);
                break;
            case SCAN_USER:
                codeBarre_scanned = "";
                Toast.makeText(getApplicationContext(), R.string.show_barcode_badge,Toast.LENGTH_SHORT).show();
                break;
            case SCAN_PRODUCT:
                codeBarre_scanned = "";
                Toast.makeText(getApplicationContext(), R.string.show_barcode_product,Toast.LENGTH_SHORT).show();
                break;
            case SEARCH_USER:
                Toast.makeText(getApplicationContext(), R.string.search_user_pending,Toast.LENGTH_SHORT).show();
                user = Utilisateur.verifieUtilisateur(codeBarre_scanned);
                //user = new Utilisateur("John Smith", "1578415156456");
                if( user != null ){
                    changeState(App_State.SEARCH_COMMAND);
                } else {
                    Toast.makeText(getApplicationContext(), R.string.any_user_finded,Toast.LENGTH_SHORT).show();
                    changeState(App_State.SCAN_USER);
                    restartCamera();
                }
                break;
            case SEARCH_COMMAND:
                Toast.makeText(getApplicationContext(), R.string.search_command_pending,Toast.LENGTH_SHORT).show();
                setContentView(R.layout.command_coming);
                textview_ptr = findViewById(R.id.username);
                textview_ptr.setText(user.getNom());
                textview_ptr = findViewById(R.id.actionPending);
                textview_ptr.setText(R.string.search_command_pending);
               // try {
                    //commande = Commande.chargerCommande(user);
                    ArrayList<Article> liste = new ArrayList<>();
                    Article a1 = new Article("Fromage Blanc","2154632156234","A","26","C",1);
                    Article a2 = new Article("Pizza","2145622145659","B","27","D",1);
                    liste.add(a1);
                    liste.add(a2);
                    commande = new Commande(13,liste,"dqsfqsf","qfqsf",user);
                    if( commande != null)
                    {
                        changeState(App_State.NAVIGATION1);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), R.string.any_command_find, Toast.LENGTH_SHORT).show();
                        changeState(App_State.SIGN_IN);
                    }/*
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
                break;
            case NAVIGATION1:
                setContentView(pfe.polytech.Vuzix_M100_entrepot.R.layout.navigation);
                textview_ptr = findViewById(R.id.product_name);
                textview_ptr.setText(commande.getArticleCourrant().getNom());
                textview_ptr = findViewById(R.id.product_aisle);
                textview_ptr.setText(commande.getArticleCourrant().getAllee());
                textview_ptr = findViewById(R.id.product_rack);
                textview_ptr.setText(commande.getArticleCourrant().getEtagere());
                textview_ptr = findViewById(R.id.product_rack_location);
                textview_ptr.setText(commande.getArticleCourrant().getEmplacementEtagere());
                textview_ptr = findViewById(R.id.username);
                textview_ptr.setText(user.getNom());
                break;
            case SEARCH_PRODUCT:
                if( commande.checkArticle(codeBarre_scanned) ) {
                    if (commande.getArticleCourrant().getQuantiteDemande() > 1) {
                        // temporairement
                        //TODO (pour val) : gestion d'un produit en plusieurs exemplaires
                        // commande.checkQuantite(commande.getArticleCourrant().getQuantiteDemande())
                        // changeState(App_State.QUANTITY_INPUT);
                        // fin temporairement
                    }
                    if ( commande.ArticleSuivant() != null) {
                        Toast.makeText(getApplicationContext(), R.string.pull_in_cart, Toast.LENGTH_SHORT).show();
                        changeState(App_State.NAVIGATION1);
                    } else {
                        changeState(App_State.NAVIGATION2);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.any_product_finded, Toast.LENGTH_SHORT).show();
                    changeState(App_State.SCAN_PRODUCT);
                    restartCamera();
                }
                break;
            case QUANTITY_INPUT:
                setContentView(pfe.polytech.Vuzix_M100_entrepot.R.layout.quantity);
                break;
            case NAVIGATION2:
                // TODO (pour val) : ecran navigation pour le depot
                //setContentView(pfe.polytech.Vuzix_M100_entrepot.R.layout.navigation);
                changeState(App_State.COMMAND_ENDED);
                break;
            case COMMAND_ENDED:
                setContentView(R.layout.command_ended);
                break;
            case SIGN_OUT:
                // TODO : gérer la déconnection
                Toast.makeText(getApplicationContext(), "Good Bye" + user.getNom(), Toast.LENGTH_SHORT).show();
                changeState(App_State.SIGN_IN);
                break;
            default:
                System.out.println(" > MainActivity.changeState() : Bad case or case unknown ");
                break;
        }
    }

    /**
     * Fonction qui permet d'activer la caméra pour le scan d'un code barre
     * @param view vue sur laquelle cette méthode a été appelée
     */
    public void scan(View view)
    {
        switch (app_state){
            case SIGN_IN:                               // si on vient de la page d'authentification
                changeState(App_State.SCAN_USER);       // on doit scanner le code barre d'un utilisateur
                break;
            case NAVIGATION1:                           // so on vient de la page de navigation
                changeState(App_State.SCAN_PRODUCT);    // on doit scanner le code barre d'un produit
                break;
        }
        startCamera();
    }

    public void startCamera(){
        zXingScannerView =new ZXingScannerView(getApplicationContext());    // création de la vue scanner ZXing
        zXingScannerView.setResultHandler(this);                            // gestion du resultat par handleResult() de cette classe
        zXingScannerView.startCamera();                                     // caméra allumé
        setContentView(zXingScannerView);                                   // changement de vue
    }
    public void restartCamera(){
        zXingScannerView.startCamera();                                     // caméra allumé
    }
    public void stopCamera(){
        zXingScannerView.stopCamera();
    }

    /**
     * Fonction qui permet de revenir à l'état précedent et effectue les traitements nécessaires correspondant
     */
    public void cancel(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed(){
        switch (app_state) {
            case SIGN_IN:
                onStop();
                onDestroy();
                break;
            case SEARCH_COMMAND:
                changeState(App_State.SIGN_IN);
            case SCAN_USER:
                stopCamera();
                changeState(App_State.SIGN_IN);
                break;
            case SCAN_PRODUCT:
                stopCamera();
                changeState(App_State.NAVIGATION1);
                break;
        }
    }

    public void nextStep(View view){
        switch (app_state) {
            case SEARCH_COMMAND:
                changeState(App_State.NAVIGATION1);
                break;
        }
    }

    public void newCommand(View view){ changeState(App_State.SEARCH_COMMAND);}

    public void signOut(View view){ changeState(App_State.SIGN_OUT);}














    /**
     * Fonction lorsqu'on sort de l'application en cours vers une autre appli smartphone
     */
    @Override
    protected void onPause() {
        super.onPause();
        switch (app_state){
            case SCAN_USER:                         // si on quitte l'appli en cours de scan
                zXingScannerView.stopCamera();      // alors on arrete la camera
                break;
            case SCAN_PRODUCT:                      // si on quitte l'appli en cours de scan
                zXingScannerView.stopCamera();      // alors on arrete la camera
                break;
        }
    }



    /**
     * Fonction lorsqu'on revient sur l'application en cours
     */
    @Override
    protected void onResume(){
        super.onResume();
        switch (app_state){
            case SCAN_USER:                         // si on quitte l'appli en cours de scan
                zXingScannerView.startCamera();     // alors on arrete la camera
                break;
            case SCAN_PRODUCT:                      // si on quitte l'appli en cours de scan
                zXingScannerView.startCamera();     // alors on arrete la camera
                break;
        }
    }

    /**
     * Fonction lorsqu'on revient sur l'application en cours
     */
    @Override
    protected void onRestart(){
        super.onRestart();
        switch (app_state){
            case SCAN_USER:                         // si on quitte l'appli en cours de scan
                zXingScannerView.startCamera();     // alors on arrete la camera
                break;
            case SCAN_PRODUCT:                      // si on quitte l'appli en cours de scan
                zXingScannerView.startCamera();     // alors on arrete la camera
                break;
        }
    }





}
