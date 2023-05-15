package esir.progm.untitledsharkgames.jeux.sharkSlap;

import android.content.ComponentCallbacks2;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.ArrayList;

import esir.progm.untitledsharkgames.MusicPlayer;
import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.interfaces.Game;

public class SharkSlap extends Game {
    /*                    hide UI parameters                    */
    private int hideSystemBars = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    private boolean isOnBackground = false;

    /*                    activity parameters                   */
    private int score;
    private TextView score_text;
    private ArrayList<Place> places;
    private MediaPlayer mainMP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Init activity and disable UI
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_shark_slap);
        View decor = getWindow().getDecorView();

        decor.setOnSystemUiVisibilityChangeListener(visibility -> {
            if(visibility==0) {decor.setSystemUiVisibility(hideSystemBars);}
        });

        mainMP = MediaPlayer.create(getApplicationContext(), R.raw.shark_slap);

        MusicPlayer.getInstance().stop();

        launch();
    }

    @Override
    public void launch() {
        // Set all sharks
        places = new ArrayList<>();
        places.add(new Place(findViewById(R.id.shark1_1), this));
        places.add(new Place(findViewById(R.id.shark1_2), this));
        places.add(new Place(findViewById(R.id.shark1_3), this));
        places.add(new Place(findViewById(R.id.shark2_1), this));
        places.add(new Place(findViewById(R.id.shark2_2), this));
        places.add(new Place(findViewById(R.id.shark2_3), this));
        places.add(new Place(findViewById(R.id.shark3_1), this));
        places.add(new Place(findViewById(R.id.shark3_2), this));
        places.add(new Place(findViewById(R.id.shark3_3), this));

        // Init elements
        score = 0;
        score_text = findViewById(R.id.score_text);
        score_text.setText(score+"");

        // Create new media player
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.shark_slap_init);

        mediaPlayer.setOnCompletionListener(mediaPlayer1 -> {
            mainMP.start();
            new SharkSlapGame(SharkSlap.this, getApplicationContext(), places).execute();
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
        MediaPlayer.create(getApplicationContext(), R.raw.score_up).start();
        score_text.setText(score+"");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainMP.stop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(hideSystemBars);
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