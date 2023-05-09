package esir.progm.untitledsharkgames.jeux.sharkSlap;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentCallbacks2;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import esir.progm.untitledsharkgames.MusicPlayer;
import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.interfaces.Game;

public class SharkSlap extends AppCompatActivity implements Game {
    private int hideSystemBars = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    private boolean isOnBackground = false;

    private int score;
    private TextView score_text;
    private ArrayList<Place> places;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_shark_slap);
        View decor = getWindow().getDecorView();
        decor.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener(){
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility==0) {
                    decor.setSystemUiVisibility(hideSystemBars);
                }
            }
        });

        launch();
    }

    @Override
    public void launch() {
        MusicPlayer.getInstance().stop();
        places = new ArrayList<Place>();
        places.add(new Place("shark1_1", findViewById(R.id.shark1_1), this));
        places.add(new Place("shark1_2", findViewById(R.id.shark1_2), this));
        places.add(new Place("shark1_3", findViewById(R.id.shark1_3), this));
        places.add(new Place("shark2_1", findViewById(R.id.shark2_1), this));
        places.add(new Place("shark2_2", findViewById(R.id.shark2_2), this));
        places.add(new Place("shark2_3", findViewById(R.id.shark2_3), this));
        places.add(new Place("shark3_1", findViewById(R.id.shark3_1), this));
        places.add(new Place("shark3_2", findViewById(R.id.shark3_2), this));
        places.add(new Place("shark3_3", findViewById(R.id.shark3_3), this));

        score = 0;
        score_text = findViewById(R.id.score_text);
        score_text.setText(score+"");

        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.shark_slap_init);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                new SharkSlapGame(SharkSlap.this, getApplicationContext(), places).execute();
            }
        });

        mediaPlayer.start();
    }

    @Override
    public int getScore() {
        return score;
    }

    public void addScore(int quantity, boolean substract) {
        if(substract) {
            if ((score - quantity) < 0) {
                score = 0;
            } else {
                score -= quantity;
            }
        } else {
            score += quantity;
        }
        score_text.setText(score+"");
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
        MusicPlayer.getInstance().resume();
        isOnBackground = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
//        MusicPlayer.getInstance().stop();
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