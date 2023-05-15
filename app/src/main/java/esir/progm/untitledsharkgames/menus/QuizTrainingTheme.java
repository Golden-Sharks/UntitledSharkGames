package esir.progm.untitledsharkgames.menus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;

import esir.progm.untitledsharkgames.MusicPlayer;
import esir.progm.untitledsharkgames.jeux.quiz.QuizActivity;
import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.ManageFiles;

public class QuizTrainingTheme extends AppCompatActivity {
    /*                    hide UI parameters                    */
    private int hideSystemBars = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

    private boolean isOnBackground = false;

    private ArrayList<String> infos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_quiz_training_theme);

        View decor = getWindow().getDecorView();
        decor.setOnSystemUiVisibilityChangeListener(visibility -> {
            if(visibility==0) {
                decor.setSystemUiVisibility(hideSystemBars);
            }
        });

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

        Button progm = findViewById(R.id.progm);
        progm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWithTheme("progm");
            }
        });

        Button requin = findViewById(R.id.requin);
        requin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchWithTheme("requin");
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
        intent.putExtras(b);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isOnBackground) {
            MusicPlayer.getInstance().pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isOnBackground) {
            MusicPlayer.getInstance().stop();
            MusicPlayer.getInstance().play(getApplicationContext(), R.raw.main_menu, true);
        } else {
            MusicPlayer.getInstance().resume();
            isOnBackground = false;
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN ||
                level == ComponentCallbacks2.TRIM_MEMORY_BACKGROUND ||
                level == ComponentCallbacks2.TRIM_MEMORY_MODERATE ||
                level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE) {
            isOnBackground = true;
            onPause(); // https://i.kym-cdn.com/photos/images/original/000/639/420/094.gif
        }
    }
}