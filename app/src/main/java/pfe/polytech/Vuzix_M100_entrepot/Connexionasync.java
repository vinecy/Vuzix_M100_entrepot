package pfe.polytech.Vuzix_M100_entrepot;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Vincent on 07/12/2017.
 */

public class Connexionasync extends AsyncTask<URL, Integer, String> {

    private TextView helloTextView;

    Connexionasync(TextView h){
        helloTextView=h;
    }

    @Override
    protected String doInBackground(URL... urls) {

        //creation connexion au seveur
        //TODO généraliser le serveur grâce a URL
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet("http://bartholomeau.fr/recevoir_commande.php?cb=978020137962");
        HttpResponse response = null;
        String result;

        try {
            //connexion
            response = client.execute(request);
            Log.d("status reponse", String.valueOf(response.getStatusLine()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //lecture de reponse
        BufferedReader rd = null;
        try {
            assert response != null;
            rd = new BufferedReader
                    (new InputStreamReader(
                            response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String line ;
        result = "";
        try {
            assert rd != null;
            while ((line = rd.readLine()) != null) {
                //stockage de la reponse
                result=result.concat(line);
                Log.d("test line",line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected void onPostExecute(String result) {
        Log.d("result",result);
        //modification du texte de l'activité
        helloTextView.setText(result);
    }
}
