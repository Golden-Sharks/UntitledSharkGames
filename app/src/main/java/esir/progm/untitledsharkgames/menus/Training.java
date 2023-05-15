package esir.progm.untitledsharkgames.menus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import esir.progm.untitledsharkgames.jeux.feedTheShark.FeedTheShark;
import esir.progm.untitledsharkgames.MusicPlayer;
import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.jeux.sharkSlap.SharkSlap;
import esir.progm.untitledsharkgames.jeux.WhrilOtter.WhrilOtter;
import esir.progm.untitledsharkgames.jeux.spinTheShark.SpinTheShark;

public class Training extends AppCompatActivity {

    private int hideSystemBars = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

    private boolean isOnBackground = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_training);

        View decor = getWindow().getDecorView();
        decor.setOnSystemUiVisibilityChangeListener(visibility -> {
            if (visibility == 0) {
                decor.setSystemUiVisibility(hideSystemBars);
            }
        });

        setButtons();
    }

    private void setButtons() {
        ImageButton exit = findViewById(R.id.exit);
        exit.setOnClickListener(v -> finish());

        Button quiz = findViewById(R.id.quiz);
        quiz.setOnClickListener(v -> startActivity(new Intent(Training.this, QuizTrainingTheme.class)));

        Button shakslap = findViewById(R.id.sharkslap);
        shakslap.setOnClickListener(view -> startActivity(new Intent(Training.this, SharkSlap.class)));

        Button whirlOtter = findViewById(R.id.WhirlOtter);
        whirlOtter.setOnClickListener(view -> startActivity(new Intent(Training.this, WhrilOtter.class)));

        Button feedSkark = findViewById(R.id.FeedSkark);
        feedSkark.setOnClickListener(view -> startActivity(new Intent(Training.this, FeedTheShark.class)));

        Button spinShark = findViewById(R.id.spin);
        spinShark.setOnClickListener(view -> startActivity(new Intent(Training.this, SpinTheShark.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicPlayer.getInstance().resume();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(hideSystemBars);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN ||
                level == ComponentCallbacks2.TRIM_MEMORY_BACKGROUND ||
                level == ComponentCallbacks2.TRIM_MEMORY_MODERATE ||
                level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE) {
            isOnBackground = true;
            onPause(); // https://i.kym-cdn.com/photos/images/original/000/639/420/094.gif
        }
    }
}