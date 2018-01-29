

package pfe.polytech.Vuzix_M100_entrepot;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

import pfe.polytech.Vuzix_M100_entrepot.model.Article;
import pfe.polytech.Vuzix_M100_entrepot.model.Commande;
import pfe.polytech.Vuzix_M100_entrepot.model.Utilisateur;



/**
 * Classe lançant le code de l'application (ACTIVITE PRINCIPAL)
 */
public class MainActivity extends Activity implements ZBarScannerView.ResultHandler
{
    /** Code correspondant à la caméra activé*/
    private static final int ZBAR_CAMERA_PERMISSION = 1;

    // ATTRIBUTS
    /**  Elements du MODELE : Singleton associant l'utilisateur à une commande*/
    private UserCommandeSingleton userCmdObj = UserCommandeSingleton.getSingleton();
    // Elements du CONTROLEUR
    /** Singleton possedant l'état en cours*/
    private EtatSingleton etatObj = EtatSingleton.getSingleton();
    /** Etat de l'application*/
    private EtatSingleton.App_State app_state ;
    /** Code barre scannée*/
    private String codeBarre_scanned = "";
    /** Pointeur sur les textView des vues (IHM)*/
    private TextView textview_ptr;
    private int distance = 155;
    // Elements de la vue
    /** Boussole (non utilisé dans l'application actuel)*/
    private CompassView compassView;
    /** Gestionnaire de listener*/
    private SensorManager sensorManager;
    /** Listener pour capter les événement du gyroscope*/
    private Sensor sensor;

    /**
     * Fonction pour la boussole
     * */
    private final SensorEventListener sensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if( compassView != null ) {
                compassView.setNorthOrientation(event.values[SensorManager.DATA_X]);
            }
        }
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    /**
     * Fonction au lancement de l'application. Au lancement, on doit s'identifier en scannant le
     * badge.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        etatObj = EtatSingleton.getSingleton();                         // Récupétation de notre objet contenant l'état de l'application
        app_state = etatObj.getEtat();
        if( app_state == EtatSingleton.App_State.INIT) {                // si on crée l'activité pour la première fois (correspond à l'état INIT)
            etatObj.setEtat( EtatSingleton.App_State.SIGN_IN);          // on passe à l'état d'authentification
        }
        userCmdObj = UserCommandeSingleton.getSingleton();
        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensors ;
        if (sensorManager != null) {
            sensors = sensorManager.getSensorList(Sensor.TYPE_ORIENTATION);
            if(sensors.size() > 0) {
                sensor = sensors.get(0);
            }
        }
        changeState( );                                                 // Application changement d'état
    }

    /**
     * Gestionnaire de resultat de l'application. Appeler lors le scan a trouvé un code barre.
     * badge
     */
    @Override
    public void handleResult( Result result) {}

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
                break;
            case SCAN_PRODUCT:
                codeBarre_scanned = "";
                break;
            case SEARCH_USER:
                setContentView(R.layout.command_coming);
                textview_ptr = findViewById(R.id.actionPending);
                textview_ptr.setText(R.string.search_user_pending);
                codeBarre_scanned = getIntent().getStringExtra("CODE_BARRE");
                // SI AVEC CONNECTION SERVEUR : DECOMENTER LA LIGNE EN-DESSOUS
                Utilisateur user = Utilisateur.verifieUtilisateur(codeBarre_scanned);
                // SI SANS CONNECTION SERVEUR : DECOMMENTER LA LIGNE EN-DESSOUS
                // Utilisateur user = new Utilisateur("John Smith", codeBarre_scanned);
                if( user != null ){             // si utilisateur trouvé
                    userCmdObj.setUtilisateur(user);
                    Toast.makeText(getApplicationContext(), "Welcome " + userCmdObj.getUtilisateur().getNom().toString(), Toast.LENGTH_SHORT).show();
                    etatObj.setEtat( EtatSingleton.App_State.SEARCH_COMMAND);
                    changeState( );
                } else {                        // sinon on relance le scan
                    Toast.makeText(getApplicationContext(), R.string.any_user_finded,Toast.LENGTH_SHORT).show();
                    etatObj.setEtat( EtatSingleton.App_State.SCAN_USER);
                    changeState( );
                    lancerScan();
                }
                break;
            case SEARCH_COMMAND:
                setContentView(R.layout.command_coming);
                textview_ptr = findViewById(R.id.username);
                textview_ptr.setText(userCmdObj.getUtilisateur().getNom());
                textview_ptr = findViewById(R.id.actionPending);
                textview_ptr.setText(R.string.search_command_pending);
                try {
                    Commande commandeTmp = Commande.chargerCommande(userCmdObj.getUtilisateur());

                    // SANS SERVEUR
                    /*
                    ArrayList<Article> liste = new ArrayList<>();
                    Article a1 = new Article("Fromage Blanc","2154632156234","A","26","C",1);
                    Article a2 = new Article("Pizza","2145622145659","B","27","D",3);
                    liste.add(a1);
                    liste.add(a2);
                    Commande commandeTmp;
                    commandeTmp = new Commande(13,liste,"dqsfqsf","qfqsf",userCmdObj.getUtilisateur());
                    */

                    //Charge la commande en cours
                    userCmdObj.setCommande( commandeTmp);

                    if( commandeTmp != null){
                        userCmdObj.setCommande(commandeTmp);
                        etatObj.setEtat( EtatSingleton.App_State.NAVIGATION1);
                        changeState( );
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.any_command_find, Toast.LENGTH_SHORT).show();
                        etatObj.setEtat( EtatSingleton.App_State.SIGN_IN);
                        changeState();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case NAVIGATION1:
                setContentView(pfe.polytech.Vuzix_M100_entrepot.R.layout.navigation);
                compassView = (CompassView)findViewById(R.id.boussole);
                textview_ptr = findViewById(R.id.product_name);
                textview_ptr.setText( userCmdObj.getCommande().getArticleCourrant().getNom());
                textview_ptr = findViewById(R.id.product_location);
                textview_ptr.setText( userCmdObj.getCommande().getArticleCourrant().getAllee()
                                    + " - " + userCmdObj.getCommande().getArticleCourrant().getEtagere()
                                    + " - " + userCmdObj.getCommande().getArticleCourrant().getEmplacementEtagere());
                textview_ptr = findViewById(R.id.product_quantity);
                textview_ptr.setText( "x " + userCmdObj.getCommande().getArticleCourrant().getQuantiteDemande());
                textview_ptr = findViewById(R.id.product_ref);
                textview_ptr.setText( "CodeBarre : " + userCmdObj.getCommande().getArticleCourrant().getCodeBarre());
                textview_ptr = findViewById(R.id.distance);
                textview_ptr.setText( "CodeBarre : " + distance);
                byte[] decodedString = Base64.decode(userCmdObj.getCommande().getArticleCourrant().getImage(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                ImageView img = (ImageView)findViewById(R.id.imgProduit);
                img.setImageBitmap(decodedByte);

                break;
            case SEARCH_PRODUCT:
                codeBarre_scanned = getIntent().getStringExtra("CODE_BARRE");
                if( userCmdObj.getCommande().checkArticle(codeBarre_scanned)){
                    if (userCmdObj.getCommande().getArticleCourrant().getQuantiteDemande() > 1) {
                        etatObj.setEtat( EtatSingleton.App_State.QUANTITY_INPUT);
                        changeState();
                    } else {
                        userCmdObj.getCommande().checkQuantite(1);
                        if (userCmdObj.getCommande().ArticleSuivant() != null) {
                            Toast.makeText(getApplicationContext(), R.string.pull_in_cart, Toast.LENGTH_SHORT).show();
                            etatObj.setEtat(EtatSingleton.App_State.NAVIGATION1);
                            changeState();
                        } else {
                            etatObj.setEtat(EtatSingleton.App_State.NAVIGATION2);
                            changeState();
                        }
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.any_product_finded, Toast.LENGTH_SHORT).show();
                    etatObj.setEtat( EtatSingleton.App_State.SCAN_PRODUCT);
                    changeState( );
                    lancerScan();
                }
                break;
            case QUANTITY_INPUT:
                setContentView(pfe.polytech.Vuzix_M100_entrepot.R.layout.quantity);
                textview_ptr = findViewById(R.id.product_name);
                textview_ptr.setText( userCmdObj.getCommande().getArticleCourrant().getNom());
                textview_ptr = findViewById(R.id.product_location);
                textview_ptr.setText( userCmdObj.getCommande().getArticleCourrant().getAllee()
                        + " - " + userCmdObj.getCommande().getArticleCourrant().getEtagere()
                        + " - " + userCmdObj.getCommande().getArticleCourrant().getEmplacementEtagere());
                textview_ptr = findViewById(R.id.product_quantity);
                textview_ptr.setText( "x " + userCmdObj.getCommande().getArticleCourrant().getQuantiteDemande());
                textview_ptr = findViewById(R.id.product_ref);
                textview_ptr.setText( "CodeBarre : " + userCmdObj.getCommande().getArticleCourrant().getCodeBarre());
                final EditText input = findViewById(R.id.nbProduitPris);
                input.setTextColor(getResources().getColor(R.color.textColor));
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                input.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if(event.getAction() == KeyEvent.ACTION_DOWN
                        && keyCode == KeyEvent.KEYCODE_ENTER){
                            input.setEnabled(false);
                            input.setEnabled(true);
                            nextStep(v);
                            return true;
                        }
                        return false;
                    }
                });
                break;
            case NAVIGATION2:
                setContentView(pfe.polytech.Vuzix_M100_entrepot.R.layout.navigation);
                compassView = (CompassView)findViewById(R.id.boussole);
                textview_ptr = findViewById(R.id.product_name);
                textview_ptr.setText( R.string.goto_depot );
                textview_ptr = findViewById(R.id.product_location);
                textview_ptr.setText( userCmdObj.getCommande().getDepot() );
                textview_ptr = findViewById(R.id.product_quantity);
                textview_ptr.setText( "" );
                textview_ptr = findViewById(R.id.product_ref);
                textview_ptr.setText( "" );
                textview_ptr = findViewById(R.id.distance);
                textview_ptr.setText( distance + "m");
                Button missingBtn = findViewById(R.id.missingButton);
                missingBtn.setVisibility(View.INVISIBLE);
                break;
            case COMMAND_ENDED:
                setContentView(R.layout.command_ended);
                break;
            case SIGN_OUT:
                Toast.makeText(getApplicationContext(), "Good Bye " + userCmdObj.getUtilisateur().getNom().toString(), Toast.LENGTH_SHORT).show();
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
                changeState( );                         // on doit scanner le code barre d'un utilisateur
                break;
            case NAVIGATION1:                           // so on vient de la page de navigation
                etatObj.setEtat( EtatSingleton.App_State.SCAN_PRODUCT);
                changeState( );                         // on doit scanner le code barre d'un produit
                break;
        }
        lancerScan();                                   // Lancement du scan via la camera
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
        }
    }

    /*-----------------------------------  CAMERA CODE -----------------------------------------*/
    /**
     * Fonction pour lancer le scan de code barre via l'activité Scan.
     */
    public void lancerScan()
    {
        // Si la permission de la camera n'est pas accordé
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
               != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, ZBAR_CAMERA_PERMISSION);
        } else {                                  // Sinon lancer le scanner
            Intent scanActivityIntent = new Intent(this, ScanActivity.class);
            this.startActivity( scanActivityIntent);

            finish();
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
            case INIT:
            case SIGN_IN:
                finish();
                break;
            case SEARCH_USER:
            case SEARCH_COMMAND:
                etatObj.setEtat( EtatSingleton.App_State.SIGN_IN);
                changeState( );
                break;
            case NAVIGATION1:
            case NAVIGATION2:
                break;
            case QUANTITY_INPUT:
                etatObj.setEtat( EtatSingleton.App_State.NAVIGATION1);
                changeState();
                break;
            case COMMAND_ENDED:
                etatObj.setEtat( EtatSingleton.App_State.SIGN_OUT);
                break;
        }
    }

    public void nextStep(View view){
        switch (app_state) {
            case SEARCH_COMMAND:
                etatObj.setEtat( EtatSingleton.App_State.NAVIGATION1);
                changeState();
                break;
            case NAVIGATION1:
                scan(view);
                break;
            case NAVIGATION2:
                etatObj.setEtat( EtatSingleton.App_State.COMMAND_ENDED);
                changeState();
                break;
            case QUANTITY_INPUT:
                EditText editText = findViewById(R.id.nbProduitPris);
                if( !editText.getText().toString().equals("")){
                    Integer value = Integer.valueOf(editText.getText().toString());
                    if( value <= userCmdObj.getCommande().getArticleCourrant().getQuantiteDemande() ){
                        userCmdObj.getCommande().checkQuantite(value);
                        if (userCmdObj.getCommande().ArticleSuivant() != null) {
                            Toast.makeText(getApplicationContext(), R.string.pull_in_cart, Toast.LENGTH_SHORT).show();
                            etatObj.setEtat(EtatSingleton.App_State.NAVIGATION1);
                            changeState();
                        } else {
                            etatObj.setEtat(EtatSingleton.App_State.NAVIGATION2);
                            changeState();
                        }
                    } else {
                        editText.setText("");
                        Toast.makeText(getApplicationContext(), "IMPOSSIBLE " + value + " > " + userCmdObj.getCommande().getArticleCourrant().getQuantiteDemande() , Toast.LENGTH_SHORT).show();
                    }
                } else {
                    editText.setText("");
                }
                break;
        }
    }

    public void newCommand(View view){
        etatObj.setEtat( EtatSingleton.App_State.SEARCH_COMMAND);
        changeState( );
    }

    public void signOut(View view){
        etatObj.setEtat( EtatSingleton.App_State.SIGN_OUT);
        changeState( );
    }

    /**
     * Fonction appelé lorsqu'un article est manquant.
     * @param view
     */
    public void articleManquant(View view){
        userCmdObj.getCommande().checkQuantite(0);
        if (userCmdObj.getCommande().ArticleSuivant() != null) {
            etatObj.setEtat(EtatSingleton.App_State.NAVIGATION1);
            changeState();
        } else {
            etatObj.setEtat(EtatSingleton.App_State.NAVIGATION2);
            changeState();
        }
    }

    /**
     * Permet de quitter l'application. Appelé par appui sur buttonEXIT de la vue activity_main
     * @param view vue où est appelé la méthode
     */
    public void exit(View view){
        Toast.makeText(getApplicationContext(), R.string.exit_pending, Toast.LENGTH_SHORT).show();
        //finish();
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
    }

    // GESTION DE L'APPLICATION

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume(){
        super.onResume();
        // Lie evenement de la boussole numérique au listener
        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onStop(){
        super.onStop();
        // Retire lien entre listener et evenements de la boussole numérique
        sensorManager.unregisterListener(sensorListener);
    }

    @Override
    protected void onRestart(){
        super.onRestart();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}