

package pfe.polytech.Vuzix_M100_entrepot;
import com.google.zxing.*;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.view.View;
import android.widget.Toast;
import android.widget.LinearLayout;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;

//import com.vuzix.speech.VoiceControl;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


enum App_State{
    AUTHENTIFICATION,
    AUTH_SCAN,
    SEARCH_COMMAND,
    NAVIGATION,
    PRODUCT_SCAN,
    QUANTITY_INPUT,
    COMMAND_ENDED
}

/**
 * Classe lançant le code de l'application
 */
public class MainActivity extends Activity implements ZXingScannerView.ResultHandler
{
    private App_State app_state = null;             // Etat de l'application
    private ZXingScannerView zXingScannerView;      // Vue pour le scan du code-barre

    /**
     * Fonction au lancement de l'application. Au lancement, on doit s'identifier en scannant le
     * badge
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app_state = App_State.AUTHENTIFICATION ;
        setContentView(pfe.polytech.Vuzix_M100_entrepot.R.layout.activity_main);
    }

    /**
     * Fonction qui permet d'activer la caméra pour le scan d'un code barre pour s'identifier
     * @param view
     */
    public void signIn(View view)
    {
        app_state = App_State.AUTH_SCAN ;
        zXingScannerView =new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    /**
     * Fonction qui permet d'activer la caméra pour le scan d'un code barre pour identifier
     * le produit
     * @param view
     */
    public void scan(View view)
    {
        app_state = App_State.PRODUCT_SCAN ;
        zXingScannerView =new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    public void searchCommand(){
        app_state = App_State.SEARCH_COMMAND ;
        setContentView(pfe.polytech.Vuzix_M100_entrepot.R.layout.command_coming);
        //TODO: CHERCHER LA COMMANDE SUR LE SERVEUR
        // ...
        //TODO: TRACER LA NAVIGATION
        // ...
        //TODO: SWITCHER SUR LE DISPLAY NAVIGATION
        // ...
    }

    /**
     * Fonction lorsqu'on sort de l'application en cours vers une autre appli smartphone
     */
    @Override
    protected void onPause() {
        super.onPause();
        switch (app_state){
            case AUTH_SCAN:                         // si on quitte l'appli en cours de scan
                zXingScannerView.stopCamera();      // alors on arrete la camera
                break;
            case PRODUCT_SCAN:                      // si on quitte l'appli en cours de scan
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
            case AUTH_SCAN:                         // si on quitte l'appli en cours de scan
                zXingScannerView.startCamera();     // alors on arrete la camera
                break;
            case PRODUCT_SCAN:                      // si on quitte l'appli en cours de scan
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
            case AUTH_SCAN:                         // si on quitte l'appli en cours de scan
                zXingScannerView.startCamera();     // alors on arrete la camera
                break;
            case PRODUCT_SCAN:                      // si on quitte l'appli en cours de scan
                zXingScannerView.startCamera();     // alors on arrete la camera
                break;
        }
    }

    @Override
    public void handleResult(Result result) {
        zXingScannerView.resumeCameraPreview(this);
        zXingScannerView.stopCamera();
        Toast.makeText(getApplicationContext(),"Code Barre : " + result.getText(),Toast.LENGTH_SHORT).show();
        switch (app_state) {
            case AUTH_SCAN:
                searchCommand();
                break;
            case PRODUCT_SCAN:
                //TODO: gerer le resultat du scan
                // ...
                break;
            default:
                app_state = App_State.AUTHENTIFICATION ;
                setContentView(pfe.polytech.Vuzix_M100_entrepot.R.layout.activity_main);
                break;
        }
    }
}
