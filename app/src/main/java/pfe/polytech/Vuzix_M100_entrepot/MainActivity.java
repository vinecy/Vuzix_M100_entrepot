package pfe.polytech.Vuzix_M100_entrepot;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.example.vincent.hello.R;


public class MainActivity extends AppCompatActivity {

    // texte de l'activité
    TextView helloTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "le programme a démarré");
        helloTextView = findViewById(R.id.text_view_id);
        helloTextView.setText(R.string.user_greeting);

        //creation et appel de la connexion de maniere asynchrone
        Connexionasync test = new Connexionasync(helloTextView);
        test.execute();

    }
}
