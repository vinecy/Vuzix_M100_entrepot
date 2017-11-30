package com.example.vincent.hello;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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


public class MainActivity extends AppCompatActivity {

    // texte de l'activité
    TextView helloTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "le programme a démarré");
        final TextView helloTextView = findViewById(R.id.text_view_id);
        helloTextView.setText(R.string.user_greeting);

        //creation et appel de la connexion de maniere asynchrone
        Myasync test = new Myasync();
        test.execute();

    }

    class Myasync extends AsyncTask<URL, Integer, String> {

        @Override
        protected String doInBackground(URL... urls) {

            //creation connexion au seveur
            //TODO généralisé le serveur grâce a URL
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://www.bartholomeau.fr/test.php?test=ça_marche");
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
            helloTextView=findViewById(R.id.text_view_id);
            helloTextView.setText(result);
        }
    }
}
