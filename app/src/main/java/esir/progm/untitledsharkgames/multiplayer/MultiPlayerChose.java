package esir.progm.untitledsharkgames.multiplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.jeux.WhrilOtter.WhrilOtter;

public class MultiPlayerChose extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_multi_player_connection);
        Button heberger = findViewById(R.id.heberger);
        heberger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pseudoValid()) {
                    Intent intent = new Intent(MultiPlayerChose.this, MultiHeberger.class);
                    intent.putExtra("pseudo", ((EditText)findViewById(R.id.pseudo)).getText().toString());
                    startActivity(intent);
                }
            }
        });
        Button rejoindre = findViewById(R.id.rejoindre);
        rejoindre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pseudoValid()) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) !=
                            PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MultiPlayerChose.this, new String[] { Manifest.permission.RECORD_AUDIO }, 1000);
                    }
                    Intent intent = new Intent(MultiPlayerChose.this, MultiClient.class);
                    intent.putExtra("pseudo", ((EditText)findViewById(R.id.pseudo)).getText().toString());
                    startActivity(intent);
                }
            }
        });
        ImageButton back = findViewById(R.id.exit);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private boolean pseudoValid() {
        EditText pseudoView = findViewById(R.id.pseudo);
        TextView error = findViewById(R.id.error);
        String pseudo = pseudoView.getText().toString();
        if (pseudo.length()==0) {
            error.setText("Renseignez un pseudonyme");
            return false;
        }
        for (int i=0 ; i<pseudo.length() ; i++){
            if (i==25) {
                error.setText("Le pseudo est trop long\n(il doit faire moins de 25 caractères)");
                return false;
            }
            char c = pseudo.charAt(i);
            if (c==' ') {
                error.setText("Le pseudo ne doit pas contenir d'espaces");
                return false;
            }
        }
        return true;
    }
}