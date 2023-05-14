package esir.progm.untitledsharkgames.multiplayer;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.os.AsyncTask;
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
import esir.progm.untitledsharkgames.jeux.WhrilOtter.WhrilOtter;
import esir.progm.untitledsharkgames.jeux.sharkSlap.SharkSlap;

public abstract class Multijoueur extends AppCompatActivity {

    private int hideSystemBars = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

    private boolean isOnBackground = false;
    protected String[] relevant;

    protected int score = 0;
    protected String pseudonyme;
    public ActivityResultLauncher<Intent> activityResultLauncher;
    protected Server server;
    protected List<Class> games;
    protected int nb_results = 0;

    protected final Class[][] POUL = {{WhrilOtter.class},{SharkSlap.class}};//{{WhrilOtter.class},{SharkSlap.class}};
    protected int[] tirageAuSort = new int[3];

    public static boolean isScoreReceived = false;

    public abstract void setMsg(String message);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decor = getWindow().getDecorView();
        decor.setOnSystemUiVisibilityChangeListener(visibility -> {
            if(visibility==0) {
                decor.setSystemUiVisibility(hideSystemBars);
            }
        });
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

    protected void launchGames(int nb) {
        if (nb==2) {
            Intent intent = new Intent(Multijoueur.this, games.get(nb));
            String[] themes = {"starwars","pokemon","progm"};
            intent.putExtra("theme", themes[tirageAuSort[2]]);
            activityResultLauncher.launch(intent);
        } else {
            activityResultLauncher.launch(new Intent(Multijoueur.this, games.get(nb)));
        }
    }

    protected void onGameResult(ActivityResult activityResult){
        if(activityResult.getResultCode() == 78) {
            Intent intent = activityResult.getData();
            if(intent != null) {
                score += intent.getIntExtra("score", 0);
            }
            drawOwnUiScores();
            nb_results++;
            findViewById(R.id.init).setVisibility(View.GONE);
            findViewById(R.id.scoreBoard).setVisibility(View.VISIBLE);
            Communication.sendMessage("_"+pseudonyme+"_"+score);
            if (nb_results==games.size()) {
                drawScore();
            }
        }
    }

    protected void drawOwnUiScores() {
        ((TextView)findViewById(R.id.J1_pseudo)).setText(this.pseudonyme+" : "+this.score);
        drawRectangle(findViewById(R.id.J1_score), this.score);
    }

    protected void drawAdversaryUiScores(String pseudo, int score) {
        ((TextView)findViewById(R.id.J2_pseudo)).setText(this.relevant[0]+" : "+this.relevant[1]);
        drawRectangle(findViewById(R.id.J2_score), Integer.parseInt(this.relevant[1]));
    }

    protected void drawScore() {
        if (nb_results==games.size()) {
            if (this.score > Integer.parseInt(relevant[1])) ((TextView)findViewById(R.id.victory)).setText("VICTOIRE");
            else if (this.score < Integer.parseInt(relevant[1])) ((TextView)findViewById(R.id.victory)).setText("PERDU");
            else ((TextView)findViewById(R.id.victory)).setText("EX-AEQUO");

            Button home = findViewById(R.id.next);
            home.setText("HOME");
            home.setVisibility(View.VISIBLE);
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
    }

    protected void drawRectangle(ImageView rct, int score) {
        rct.getLayoutParams().width = score * rct.getMaxWidth() / 5000;
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
