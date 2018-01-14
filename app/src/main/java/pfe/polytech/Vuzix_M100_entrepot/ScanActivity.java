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
    // ATTRIBUTS
    private int myPID;
    private ZBarScannerView mScannerView;               // Objet de scan de code barre
    private static ScanActivity scanActivity = null;    // Activité de scan
    private EtatSingleton etatObj ;                     // Singleton possedant l'état en cours
    private EtatSingleton.App_State app_state ;                      // Etat de l'application

    private TextView textView_ptr;

    // METHODS
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        etatObj = EtatSingleton.getSingleton();
        app_state = etatObj.getEtat();
        scanActivity = this;
        setContentView(R.layout.scan_layout);                                   // Passe à la vue de scan
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);  // Appel le cadre où sera la caméra dans scan_layout
        mScannerView = new ZBarScannerView(this);                        // Création de l'objet scannant les codes barres
        contentFrame.addView(mScannerView);                                     // Ajoute le scan dans le cadre
    }

    @Override
    public void handleResult(Result rawResult) {
       /* Toast.makeText(this, "Contents = " + rawResult.getContents() +
                ", Format = " + rawResult.getBarcodeFormat().getName(), Toast.LENGTH_SHORT).show();*/
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.

        System.out.println("\nSCAN ACTIVITY -----------------------------------------------------------------");
        System.out.println("ETAT = "+etatObj.getEtat());
        System.out.println("SCAN ACTIVITY -----------------------------------------------------------------\n");

        Toast.makeText(getApplicationContext(),"Code Barre : " + rawResult.getContents(),Toast.LENGTH_SHORT).show();
        app_state = etatObj.getEtat();
        switch (app_state) {
            case SCAN_USER:
                textView_ptr = findViewById(R.id.messageScan);
                textView_ptr.setText("show_barcode_badge");
                etatObj.setEtat( EtatSingleton.App_State.SEARCH_USER);
                break;
            case SCAN_PRODUCT:
                textView_ptr = findViewById(R.id.messageScan);
                textView_ptr.setText("Commande ==> TODO mettre nom produit");
                etatObj.setEtat( EtatSingleton.App_State.SEARCH_PRODUCT);
                break;
        }

        System.out.println("\nSCAN ACTIVITY after switch-----------------------------------------------------------------");
        System.out.println("ETAT = "+etatObj.getEtat());
        System.out.println("SCAN ACTIVITY  after switch -----------------------------------------------------------------\n");

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ScanActivity.this);
            }
        }, 2000);

        Intent mainIntent = new Intent(this, MainActivity.class);   // Lance main activity (l'activité principal)
        mainIntent.putExtra("CODE_BARRE", rawResult.getContents());        // Ajoute le code barre scanné dans l'information à transmettre à l'autre activité
        this.startActivity( mainIntent);                                         // retour à l'activité principale
        finish();                                                                // fin de l'activité de scan
        //myPID = android.os.Process.myPid();
        //android.os.Process.killProcess(myPID);
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
        Intent mainIntent = new Intent(this, MainActivity.class);   // Lance main activity (l'activité principal)
        this.startActivity( mainIntent);                                         // retour à l'activité principale
        finish();
        //myPID = android.os.Process.myPid();
        //android.os.Process.killProcess(myPID);
    }

    @Override
    protected void onPause() {
        Toast.makeText(getApplicationContext(), "ScanActivity.onPause()", Toast.LENGTH_SHORT).show();
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    protected void onResume(){
        Toast.makeText(getApplicationContext(), "ScanActivity.onResume()", Toast.LENGTH_SHORT).show();
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    protected void onStop(){
        Toast.makeText(getApplicationContext(), "ScanActivity.onStop()", Toast.LENGTH_SHORT).show();
        super.onStop();
    }

    @Override
    protected void onRestart(){
        Toast.makeText(getApplicationContext(), "ScanActivity.onRestart()", Toast.LENGTH_SHORT).show();
        super.onRestart();
    }

    @Override
    protected void onDestroy(){
        Toast.makeText(getApplicationContext(), "ScanActivity.onDestroy()", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}

