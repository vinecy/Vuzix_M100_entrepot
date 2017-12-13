package pfe.polytech.Vuzix_M100_entrepot;

import android.os.AsyncTask;
import android.util.Log;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Vincent on 12/12/2017.
 */

public class Connexionasync extends AsyncTask<String, Integer, String> {

      String result;

    public Connexionasync() {
        result="";
    }

    /**
     * Récupère le resultat de la requete
     * @return le resultat de la requete ( String)
     */
    public String getResult() {
        return result;
    }

    @Override
    protected  String doInBackground(String... adress) {

        Log.d("tag", "adress "+adress[0]);

        //creation connexion au seveur
        //TODO généraliser le serveur grâce a URL
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(adress[0]);
        HttpResponse response = null;

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
        try {
            assert rd != null;
            while ((line = rd.readLine()) != null) {
                //stockage de la reponse
                this.result=this.result.concat(line);
                Log.d("test line",line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this.result;
    }
    @Override
    protected void onPostExecute(String result) {
        Log.d("result",result);
        //modification du texte de l'activité
       // helloTextView.setText(result);
    }
}
