package esir.progm.untitledsharkgames.multiplayer;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import esir.progm.untitledsharkgames.Communication.Server;
import esir.progm.untitledsharkgames.MusicPlayer;
import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.ScoreDB;
import esir.progm.untitledsharkgames.jeux.WhrilOtter.WhrilOtter;
import esir.progm.untitledsharkgames.jeux.feedTheShark.FeedTheShark;
import esir.progm.untitledsharkgames.jeux.sharkSlap.SharkSlap;
import esir.progm.untitledsharkgames.jeux.spinTheShark.SpinTheShark;

/**
 * Classe abstraite gérant les comportements communs au client et à l'hébergeur
 */
public abstract class Multijoueur extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private int hideSystemBars = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    private boolean isOnBackground = false;
    protected String[] relevant; // Stocks les données importantes des messages
    protected int score = 0;
    protected String pseudonyme;
    public ActivityResultLauncher<Intent> activityResultLauncher;
    protected Server server;
    protected List<Class> games;
    protected int nb_results = 0;
    protected final Class[][] POUL = {{WhrilOtter.class, FeedTheShark.class},{SpinTheShark.class, SharkSlap.class}}; // Différents jeux qu'il est possible de lancer
    protected int[] tirageAuSort = new int[3];
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
        MusicPlayer.getInstance().pause();
        server = new Server(this);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult activityResult) {
                        onGameResult(activityResult);
                    }
                });
    }

    /**
     * Lance un jeu à partir de la liste des jeux
     */
    protected void launchGames(int nb) {
        if (nb==2) {
            Intent intent = new Intent(Multijoueur.this, games.get(nb));
            String[] themes = {"starwars","pokemon","progm", "requin"};
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

    /**
     * Affiche le score du joueur local sur l'UI
     */
    protected void drawOwnUiScores() {
        ((TextView)findViewById(R.id.J1_pseudo)).setText(this.pseudonyme+" : "+this.score);
        drawRectangle(findViewById(R.id.J1_score), this.score);
        int advScore = recupAdvScoreOnUI();
        boolean toReactivate = false;
        if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
            toReactivate = true;
            mediaPlayer.stop();
        }
        if (this.score > advScore) {
            ((TextView)findViewById(R.id.victory)).setText("VICTOIRE");
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.victory_song);
        }
        else if (this.score < advScore) {
            ((TextView)findViewById(R.id.victory)).setText("PERDU");
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.defeat_song);
        }
        else {
            ((TextView)findViewById(R.id.victory)).setText("EX-AEQUO");
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.exaequo_song);
        }
        if (toReactivate) mediaPlayer.start();
    }

    private int recupAdvScoreOnUI() {
        String[] all = ((TextView)findViewById(R.id.J2_pseudo)).getText().toString().split(" ");
        String scoreAdv = "0";
        if (all.length==3) {
            scoreAdv = all[2];
        }
        return Integer.parseInt(scoreAdv);
    }

    /**
     * Affiche le score et le pseudo du joueur 2 sur l'UI
     */
    protected void drawAdversaryUiScores(String pseudo, int score) {
        ((TextView)findViewById(R.id.J2_pseudo)).setText(pseudo+" : "+score);
        drawRectangle(findViewById(R.id.J2_score), score);
        boolean toReactivate = false;
        if (mediaPlayer!=null && mediaPlayer.isPlaying()) {
            toReactivate = true;
            mediaPlayer.stop();
        }
        if (this.score > score) {
            ((TextView)findViewById(R.id.victory)).setText("VICTOIRE");
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.victory_song);
        }
        else if (this.score < score) {
            ((TextView)findViewById(R.id.victory)).setText("PERDU");
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.defeat_song);
        }
        else {
            ((TextView)findViewById(R.id.victory)).setText("EX-AEQUO");
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.exaequo_song);
        }
        if (toReactivate) mediaPlayer.start();
    }

    protected void drawScore() {
        if (nb_results==games.size()) {
            TextView victory = findViewById(R.id.victory);
            victory.setVisibility(View.VISIBLE);
            Button home = findViewById(R.id.next);
            home.setText("HOME");
            home.setVisibility(View.VISIBLE);

            mediaPlayer.start();
            home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        FileOutputStream os = getApplicationContext().openFileOutput("scores.txt", Context.MODE_PRIVATE);
                        InputStream is = getApplicationContext().getResources().openRawResource(R.raw.scores);
                        ScoreDB.getInstance(is, os).addOnLeaderboard(pseudonyme, score);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace(); // Le fichier n'existe plus ce qui n'est pas normal
                    }
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
        if (mediaPlayer!=null) mediaPlayer.stop();
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
