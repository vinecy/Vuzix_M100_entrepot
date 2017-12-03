

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


/**
 * Classe lançant le code de l'application
 */
public class MainActivity extends Activity implements ZXingScannerView.ResultHandler
{

    private ZXingScannerView zXingScannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(pfe.polytech.Vuzix_M100_entrepot.R.layout.activity_main);
    }

    /**
     * Fonction qui démarre le scan d'un code barre
     * @param view
     */
    public void scan( View view)
    {
        zXingScannerView =new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    /**
     * Fonction permettant d'arreter l'application si celle-ci est arréter
     */
    @Override
    protected void onPause() {
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        Toast.makeText(getApplicationContext(),result.getText(),Toast.LENGTH_SHORT).show();
        zXingScannerView.resumeCameraPreview(this);

    }

}
