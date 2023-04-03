package esir.progm.untitledsharkgames.menus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import esir.progm.untitledsharkgames.jeux.quiz.QuizActivity;
import esir.progm.untitledsharkgames.R;

public class QuizTrainingTheme extends AppCompatActivity {

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
                Intent intent = new Intent(QuizTrainingTheme.this, QuizActivity.class);
                Bundle b = new Bundle();
                b.putString("theme", "starwars");
                intent.putExtras(b);
                startActivity(intent);
            }
        });

        Button pokemon = findViewById(R.id.pokemon);
        pokemon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuizTrainingTheme.this, QuizActivity.class);
                Bundle b = new Bundle();
                b.putString("theme", "pokemon");
                intent.putExtras(b);
                startActivity(intent);
            }
        });
    }
}