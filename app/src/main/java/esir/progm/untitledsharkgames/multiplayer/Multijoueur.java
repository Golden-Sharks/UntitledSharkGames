package esir.progm.untitledsharkgames.multiplayer;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import esir.progm.untitledsharkgames.Communication.Server;
import esir.progm.untitledsharkgames.MusicPlayer;
import esir.progm.untitledsharkgames.R;

public abstract class Multijoueur extends AppCompatActivity {

    protected String[] relevant;
    protected boolean isOnBackground = false;

    protected int score = 0;
    protected String pseudonyme;
    public ActivityResultLauncher<Intent> activityResultLauncher;
    protected Server server;
    protected List<Class> games;
    protected int nb_results = 0;
    protected boolean isScoreReceived = false;

    public abstract void setMsg(String message);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        if (intent != null) {
            pseudonyme = intent.getStringExtra("pseudo");
        } else {
            pseudonyme = "default";
        }
        server = new Server(this);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult activityResult) {
                        onGameResult(activityResult);
                    }
                });
    }
    protected void launchGames() {
        activityResultLauncher.launch(new Intent(Multijoueur.this, games.get(0)));
    }

    protected void onGameResult(ActivityResult activityResult){
        if(activityResult.getResultCode() == 78) {
            Intent intent = activityResult.getData();
            if(intent != null) {
                score += intent.getIntExtra("score", 0);
                Communication.sendMessage("_"+pseudonyme+"_"+score);
            }
            nb_results++;
            findViewById(R.id.init).setVisibility(View.GONE);
            findViewById(R.id.scoreBoard).setVisibility(View.VISIBLE);
        }
    }

    protected void playGame(int nb) {
        ((TextView)findViewById(R.id.J2_pseudo)).setText(this.pseudonyme+" : "+this.score);
        ((TextView)findViewById(R.id.J2_pseudo)).setText(this.relevant[0]+" : "+this.relevant[1]);
        drawRectangle(findViewById(R.id.J1_score), this.score);
        drawRectangle(findViewById(R.id.J2_score), Integer.parseInt(this.relevant[1]));
        if (nb<games.size()) {
            try {
                Thread.sleep(10000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            activityResultLauncher.launch(new Intent(Multijoueur.this, games.get(nb)));
        } else {
            Button home = findViewById(R.id.home);
            home.setVisibility(View.VISIBLE);
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    protected void drawRectangle(ImageView rct, int width) {
        rct.getLayoutParams().width = width;
        rct.requestLayout();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (server!=null) server.onDestroy();
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
