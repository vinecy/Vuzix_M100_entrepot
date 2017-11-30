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

    TextView helloTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "le programme a démarré");
        final TextView helloTextView = (TextView) findViewById(R.id.text_view_id);
        helloTextView.setText(R.string.user_greeting);

        Myasync test;
        test = new Myasync(this);
        test.execute();

    }

    class Myasync extends AsyncTask<URL, Integer, String> {

        Activity mActivity;

        public Myasync(Activity activity) {
            mActivity = activity;
        }

        @Override
        protected String doInBackground(URL... urls) {

            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet("http://www.bartholomeau.fr/test.php?test=ça_marche");
            HttpResponse response = null;
            String result = null;

            try {
                response = client.execute(request);
                Log.d("reponse", String.valueOf(response.getStatusLine()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader rd = null;
            try {
                rd = new BufferedReader
                        (new InputStreamReader(

                                response.getEntity().getContent()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            String line = "";
            result = "";
            try {
                while ((line = rd.readLine()) != null) {
                    //helloTextView.append(line);
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
            helloTextView=(TextView)findViewById(R.id.text_view_id);
            helloTextView.setText(result);
        }
    }
}
