package esir.progm.untitledsharkgames.menus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import esir.progm.untitledsharkgames.PopUp;
import esir.progm.untitledsharkgames.jeux.quiz.QuizActivity;
import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.ManageFiles;

public class QuizTrainingTheme extends AppCompatActivity {

    private ArrayList<String> infos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_quiz_training_theme);

        setButtons();
    }

    private void setButtons() {
        ImageButton exit = findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Button starWars = findViewById(R.id.starwars);
        starWars.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWithTheme("starwars");
            }
        });

        Button pokemon = findViewById(R.id.pokemon);
        pokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWithTheme("pokemon");
            }
        });

        Button progM = findViewById(R.id.progM);
        progM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWithTheme("progm");
            }
        });

        Button random = findViewById(R.id.random);
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWithTheme("random");
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        readScore();
    }

    private void readScore() {
        ManageFiles mf = new ManageFiles(getApplicationContext());
        if (mf.exists("score_tmp")) {
            String score = mf.readFile("score_tmp");
            System.out.println("SCORE : "+score);
            mf.erase("score_tmp");
            Toast.makeText(getApplicationContext(), ("Votre score : "+score), Toast.LENGTH_LONG).show();
        }
    }


    private void launchWithTheme(String theme) {
        Intent intent = new Intent(QuizTrainingTheme.this, QuizActivity.class);
        Bundle b = new Bundle();
        b.putString("theme", theme);
        b.putBoolean("training", true);
        intent.putExtras(b);
        startActivity(intent);
    }
}