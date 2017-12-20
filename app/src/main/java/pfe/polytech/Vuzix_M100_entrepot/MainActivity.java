

package pfe.polytech.Vuzix_M100_entrepot;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

import pfe.polytech.Vuzix_M100_entrepot.model.Article;
import pfe.polytech.Vuzix_M100_entrepot.model.Commande;
import pfe.polytech.Vuzix_M100_entrepot.model.Utilisateur;

/**
 * Enumère les differentes états de l'application
 */
/*enum App_State{
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
}*/

/**
 * Classe lançant le code de l'application
 */
public class MainActivity extends Activity implements ZBarScannerView.ResultHandler//ZXingScannerView.ResultHandler
{
    //Code correspondant à la caméra activé
    private static final int ZBAR_CAMERA_PERMISSION = 1;


    // ATTRIBUTS
    // Elements du MODELE
    private Utilisateur user;
    private Commande commande;

    // Elements du CONTROLEUR
    //Singleton possedant l'état en cours
    private EtatSingleton etatObj = EtatSingleton.getSingleton();
    private EtatSingleton.App_State app_state = etatObj.getEtat();   // Etat de l'application
    private String codeBarre_scanned = "";          // Code barre scanné
    private TextView textview_ptr;

    /**
     * Fonction au lancement de l'application. Au lancement, on doit s'identifier en scannant le
     * badge
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        etatObj = EtatSingleton.getSingleton();
        if( etatObj.getEtat() == EtatSingleton.App_State.INIT) {
            etatObj.setEtat(EtatSingleton.App_State.SIGN_IN);
        }
        changeState( );                             // Au demarrage, on est sur la page d'authentification
    }

    /**
     * Gestionnaire de resultat de l'application. Appeler lors le scan a trouvé un code barre.
     * badge
     */
    //Todo: Enlever handleResult et le passer dans le scanActivity
    @Override
    public void handleResult( Result result) {
    }

    /**
     * Fonction qui permet de changer et effectue les traitements nécessaires correspondant
     */
    public void changeState(){
        app_state = etatObj.getEtat();
        switch (app_state) {
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
                setContentView(R.layout.command_coming);
                textview_ptr = findViewById(R.id.actionPending);
                textview_ptr.setText(R.string.search_user_pending);
                codeBarre_scanned = getIntent().getStringExtra("CODE_BARRE");
               // user = Utilisateur.verifieUtilisateur(codeBarre_scanned);
               user = new Utilisateur("John Smith", "1578415156456");
                if( user != null ){
                    etatObj.setEtat( EtatSingleton.App_State.SEARCH_COMMAND);
                    changeState( );
                } else {
                    Toast.makeText(getApplicationContext(), R.string.any_user_finded,Toast.LENGTH_SHORT).show();
                    etatObj.setEtat( EtatSingleton.App_State.SCAN_USER);
                    changeState( );
                   // restartCamera();
                    lancerScan();
                }
                break;
            case SEARCH_COMMAND:
                Toast.makeText(getApplicationContext(), R.string.search_command_pending,Toast.LENGTH_SHORT).show();
                setContentView(R.layout.command_coming);
                textview_ptr = findViewById(R.id.username);
                textview_ptr.setText(user.getNom());
                textview_ptr = findViewById(R.id.actionPending);
                textview_ptr.setText(R.string.search_command_pending);
                //try {
                    //commande = Commande.chargerCommande(user);
                    ArrayList<Article> liste = new ArrayList<>();
                    Article a1 = new Article("Fromage Blanc","2154632156234","A","26","C",1);
                    Article a2 = new Article("Pizza","2145622145659","B","27","D",1);
                    liste.add(a1);
                    liste.add(a2);
                    commande = new Commande(13,liste,"dqsfqsf","qfqsf",user);
                    if( commande != null)
                    {
                        etatObj.setEtat( EtatSingleton.App_State.NAVIGATION1);
                        changeState( );
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), R.string.any_command_find, Toast.LENGTH_SHORT).show();
                        etatObj.setEtat( EtatSingleton.App_State.SIGN_IN);
                        changeState();
                    }
               /* } catch (JSONException e) {
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
                codeBarre_scanned = getIntent().getStringExtra("CODE_BARRE");
                if( commande.checkArticle( codeBarre_scanned.toString())){
                    if (commande.getArticleCourrant().getQuantiteDemande() > 1) {
                        // temporairement
                        //TODO (pour val) : gestion d'un produit en plusieurs exemplaires
                        // commande.checkQuantite(commande.getArticleCourrant().getQuantiteDemande())
                        // changeState(App_State.QUANTITY_INPUT);
                        // fin temporairement
                    }
                    if ( commande.ArticleSuivant() != null) {
                        Toast.makeText(getApplicationContext(), R.string.pull_in_cart, Toast.LENGTH_SHORT).show();
                        etatObj.setEtat( EtatSingleton.App_State.NAVIGATION1);
                        changeState();
                    } else {
                        etatObj.setEtat( EtatSingleton.App_State.NAVIGATION2);
                        changeState();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.any_product_finded, Toast.LENGTH_SHORT).show();
                    etatObj.setEtat( EtatSingleton.App_State.SCAN_PRODUCT);
                    changeState( );
                    //restartCamera();
                    lancerScan();
                }

                break;
            case QUANTITY_INPUT:
                setContentView(pfe.polytech.Vuzix_M100_entrepot.R.layout.quantity);
                break;
            case NAVIGATION2:
                // TODO (pour val) : ecran navigation pour le depot
                //setContentView(pfe.polytech.Vuzix_M100_entrepot.R.layout.navigation);
                etatObj.setEtat( EtatSingleton.App_State.COMMAND_ENDED);
                changeState( );
                break;
            case COMMAND_ENDED:
                setContentView(R.layout.command_ended);
                break;
            case SIGN_OUT:
                // TODO : gérer la déconnection
                Toast.makeText(getApplicationContext(), "Good Bye" + user.getNom(), Toast.LENGTH_SHORT).show();
                etatObj.setEtat( EtatSingleton.App_State.SIGN_IN);
                changeState( );
                break;
            default:
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
                etatObj.setEtat( EtatSingleton.App_State.SCAN_USER);
                changeState( );       // on doit scanner le code barre d'un utilisateur
                break;
            case NAVIGATION1:                           // so on vient de la page de navigation
                etatObj.setEtat( EtatSingleton.App_State.SCAN_PRODUCT);
                changeState( );    // on doit scanner le code barre d'un produit
                break;
        }
       /* startCamera();*/

       // Lancement du scan via la camera
       lancerScan();
       //TODO: trouver commment arreter l'activité!!!
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,  String permissions[], int[] grantResults) {
        switch (requestCode) {
            case ZBAR_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(  ScanActivity.class != null) {
                        Intent intent = new Intent(this,  ScanActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(this, "Please grant camera permission to use the QR Scanner", Toast.LENGTH_SHORT).show();
                }
                return;
        }
    }

    /*-----------------------------------  CAMERA CODE -----------------------------------------*/
    /**
     * Fonction pour lancer le scan de code barre via l'activité Scan.
     *
     */
    public void lancerScan()
    {
        // Si la permission de la camera n'est pas accordé
        //Todo: ajouter un message
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
               != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        }
        // Sinon lancer le scanner
        else {

            Intent scanActivityIntent = new Intent(this, ScanActivity.class);
            this.startActivity( scanActivityIntent);
          /*  scanActivity.getInstance().finish();
           scanActivity.getInstance().onCreate(new Bundle());*/
        }

    }
    /*-----------------------------------  FIN CAMERA CODE -----------------------------------------*/

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
                etatObj.setEtat( EtatSingleton.App_State.SIGN_IN);
                changeState( );
            case SCAN_USER:
                //stopCamera();
                //Todo: stopper l'activité scan ici
                etatObj.setEtat( EtatSingleton.App_State.SIGN_IN);
                changeState( );
                break;
            case SCAN_PRODUCT:
                //stopCamera();
                //Todo: stopper l'activité scan ici
                etatObj.setEtat( EtatSingleton.App_State.NAVIGATION1);
                changeState( );
                break;
        }
    }

    public void nextStep(View view){
        switch (app_state) {
            case SEARCH_COMMAND:
                etatObj.setEtat( EtatSingleton.App_State.NAVIGATION1);
                changeState();
                break;
        }
    }

    public void newCommand(View view){
        etatObj.setEtat( EtatSingleton.App_State.SEARCH_COMMAND);
        changeState( );}

    public void signOut(View view){
        etatObj.setEtat( EtatSingleton.App_State.SIGN_OUT);
        changeState( );}














    /**
     * Fonction lorsqu'on sort de l'application en cours vers une autre appli smartphone
     */
    @Override
    protected void onPause() {
        super.onPause();
        switch (app_state){
            case SCAN_USER:                         // si on quitte l'appli en cours de scan
               //zXingScannerView.stopCamera();      // alors on arrete la camera
                //TODO= pb => replacer par la fin d'activity!
                break;
            case SCAN_PRODUCT:                      // si on quitte l'appli en cours de scan
               // zXingScannerView.stopCamera();      // alors on arrete la camera
                //TODO= pb => replacer par la fin d'activity!
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
                //zXingScannerView.startCamera();     // alors on arrete la camera
                changeState();
                break;
            case SCAN_PRODUCT:                      // si on quitte l'appli en cours de scan
                //zXingScannerView.startCamera();     // alors on arrete la camera
                changeState();
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
               // zXingScannerView.startCamera();     // alors on arrete la camera
                changeState();
                break;
            case SCAN_PRODUCT:                      // si on quitte l'appli en cours de scan
               // zXingScannerView.startCamera();     // alors on arrete la camera
                changeState();
                break;
        }
    }





}
