package pfe.polytech.Vuzix_M100_entrepot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.widget.Toast;

import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

/**
 * Classe activité lancant le scan de code barre
 * Utilise la librarie Zbar réalisé à partir de XZing
 */
public class ScanActivity extends Activity implements ZBarScannerView.ResultHandler  {

    //Objet de scan de code barre
    private ZBarScannerView mScannerView;
    //Activité de scan
    private static ScanActivity scanActivity = null;
    // Singleton de l'objet contenant l'etat
    private EtatSingleton etatObj ;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        etatObj = EtatSingleton.getSingleton();
      //  etatObj = (EtatSingleton) getApplicationContext();
        scanActivity = this;
        //Passe à la vue de scan
        setContentView(R.layout.scan_layout);
        //Appel le cadre où sera la caméra dans scan_layout
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        //Création de l'objet scannant les codes barres
        mScannerView = new ZBarScannerView(this);
        //Ajoute le scan dans le cadre
        contentFrame.addView(mScannerView);

    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
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
        EtatSingleton.App_State app_state = etatObj.getEtat();
        switch (app_state) {
            case SCAN_USER:
                etatObj.setEtat( EtatSingleton.App_State.SEARCH_USER);
                break;
            case SCAN_PRODUCT:
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

        //Lance main activity (l'activité principal)
        Intent mainIntent = new Intent(this, MainActivity.class);
        //Ajoute le code barre scanné dans l'information à transmettre à l'autre activité
        mainIntent.putExtra("CODE_BARRE", rawResult.getContents());
        this.startActivity( mainIntent);
    }

    public static ScanActivity getInstance(){
        return scanActivity;
    }
}

