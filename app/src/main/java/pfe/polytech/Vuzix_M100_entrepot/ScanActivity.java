package pfe.polytech.Vuzix_M100_entrepot;

import android.app.Activity;
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

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
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
        Toast.makeText(this, "Contents = " + rawResult.getContents() +
                ", Format = " + rawResult.getBarcodeFormat().getName(), Toast.LENGTH_SHORT).show();
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(ScanActivity.this);
            }
        }, 2000);
    }
}

