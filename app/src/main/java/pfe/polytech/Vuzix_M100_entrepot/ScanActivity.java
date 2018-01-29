package pfe.polytech.Vuzix_M100_entrepot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Classe activité lancant le scan de code barre
 * Utilise la librarie Zbar réalisé à partir de XZing
 */
public class ScanActivity extends Activity implements ZBarScannerView.ResultHandler  {

    //Attributs
    /** Objet de scan de code barre*/
    private ZBarScannerView mScannerView;
    /** Activité de scan */
    private static ScanActivity scanActivity = null;
    /** Singleton possedant l'état en cours*/
    private EtatSingleton etatObj ;
    /** Etat de l'application*/
    private EtatSingleton.App_State app_state ;
    /** Pointeur sur les textView des vues (IHM)*/
    private TextView textView_ptr;

    // METHODS
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        //Récupère les singletons
        etatObj = EtatSingleton.getSingleton();
        app_state = etatObj.getEtat();
        scanActivity = this;
        // Passe à la vue de scan
        setContentView(R.layout.scan_layout);
        // Appel le cadre où sera la caméra dans scan_layout
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        // Création de l'objet scannant les codes barres
        mScannerView = new ZBarScannerView(this);
        // Ajoute le scan dans le cadre
        contentFrame.addView(mScannerView);
        app_state = etatObj.getEtat();
        UserCommandeSingleton userCmdObj = UserCommandeSingleton.getSingleton();
        //Affichage des messages au dessus de la fenetre de scan
        switch (app_state) {
            //Etat actuel: scan de l'utilisateur
            case SCAN_USER:
                textView_ptr = findViewById(R.id.messageScan);
                textView_ptr.setTextColor( getResources().getColor(R.color.textColor));
                textView_ptr.setText( getString( R.string.show_barcode_badge));
                break;
            //Etat actuel: scan du produit
            case SCAN_PRODUCT:
                textView_ptr = findViewById(R.id.messageScan);
                textView_ptr.setTextColor( getResources().getColor(R.color.textColor));
                textView_ptr.setText( userCmdObj.getCommande().getArticleCourrant().getNom() + " : "
                        + userCmdObj.getCommande().getArticleCourrant().getCodeBarre()
                        + " x " +  userCmdObj.getCommande().getArticleCourrant().getQuantiteDemande());
                break;
        }
    }

    /**
     * Cette méthode est appelé lorsqu'un code barre a été lu
     * @param rawResult
     */
    @Override
    public void handleResult(Result rawResult) {
        //Affiche le code barre lu
        Toast.makeText(getApplicationContext(),"Code Barre : " + rawResult.getContents(),Toast.LENGTH_SHORT).show();
        app_state = etatObj.getEtat();
        //Change l'état en cours selon l'état actuel
        switch (app_state) {
            //Etat : scan du code barre de l'utilisateur (authentification)
            case SCAN_USER:
                etatObj.setEtat( EtatSingleton.App_State.SEARCH_USER);
                break;
            //Etat : scan du code barre d'un article
            case SCAN_PRODUCT:
                etatObj.setEtat( EtatSingleton.App_State.SEARCH_PRODUCT);
                break;
        }
        //Temps d'attente avant l'exécution
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ScanActivity.this);
            }
        }, 2000);

        // Lance main activity (l'activité principal)
        Intent mainIntent = new Intent(this, MainActivity.class);
        // Ajoute le code barre scanné dans l'information à transmettre à l'autre activité
        mainIntent.putExtra("CODE_BARRE", rawResult.getContents());
        // retour à l'activité principale
        this.startActivity( mainIntent);
        // fin de l'activité de scan
        finish();
    }

    public static ScanActivity getInstance(){
        return scanActivity;
    }

    // GESTION DE L'APPLICATION

    @Override
    public void onBackPressed(){
        app_state = etatObj.getEtat();
        switch (app_state) {
            case SCAN_USER:
                etatObj.setEtat( EtatSingleton.App_State.SIGN_IN);
                break;
            case SCAN_PRODUCT:
                etatObj.setEtat( EtatSingleton.App_State.NAVIGATION1);
                break;
        }
        // Lance main activity (l'activité principal)
        Intent mainIntent = new Intent(this, MainActivity.class);
        // retour à l'activité principale
        this.startActivity( mainIntent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onStop(){
        super.onStop();
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

