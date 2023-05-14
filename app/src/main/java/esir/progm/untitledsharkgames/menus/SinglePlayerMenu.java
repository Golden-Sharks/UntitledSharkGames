package esir.progm.untitledsharkgames.menus;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import esir.progm.untitledsharkgames.MusicPlayer;
import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.ScoreDB;
import esir.progm.untitledsharkgames.jeux.quiz.QuizActivity;
import esir.progm.untitledsharkgames.jeux.sharkSlap.SharkSlap;

public class SinglePlayerMenu extends AppCompatActivity {


    private int hideSystemBars = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    private boolean isOnBackground = false;

    private int nb_games;
    public ActivityResultLauncher<Intent> activityResultLauncher;
    private List<Class> games;
    private final int MAX_GAMES_NUMBER = 99;
    private RelativeLayout items;
    private RelativeLayout finish_screen;
    private LinearLayout score_save;
    private Button score_submit;
    private EditText username_feild;
    private TextView final_score;

    private int score;
    private int nb_results;

    FileOutputStream os;
    InputStream is;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_single_player_menu);


        View decor = getWindow().getDecorView();
        decor.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener(){
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility==0) {
                    decor.setSystemUiVisibility(hideSystemBars);
                }
            }
        });
        nb_games = 1;
        score = 0;
        games = createGameList();
        nb_results = 0;

        TextView up = findViewById(R.id.up_games);
        TextView down = findViewById(R.id.down_games);
        TextView nb_game_text = findViewById(R.id.nb_games);
        items = findViewById(R.id.items);
        finish_screen = findViewById(R.id.score_screen);
        score_save = findViewById(R.id.score_save);
        score_submit = findViewById(R.id.save_score);
        username_feild = findViewById(R.id.username_text);
        final_score = findViewById(R.id.score);;

        is = getApplicationContext().getResources().openRawResource(R.raw.scores);
        try {
            os = getApplicationContext().openFileOutput("scores.txt", Context.MODE_PRIVATE);
        } catch (Exception e) {
            System.out.println("Ficher scores plus là !");
        }

        score_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ScoreDB.getInstance(is, os).addOnLeaderboard(username_feild.getText().toString(), score);
                finish();
            }
        });

        up.setOnClickListener(view -> {
            if(nb_games < MAX_GAMES_NUMBER) {
                nb_games++;
                nb_game_text.setText(nb_games+"");
            }
        });

        down.setOnClickListener(view -> {
            if(nb_games > 0) {
                nb_games--;
                nb_game_text.setText(nb_games+"");
            }
        });

        Button start = findViewById(R.id.start_singlePlayer);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                games = createGameList();
                if(games.get(0) == QuizActivity.class) {
                    Intent intent = new Intent(SinglePlayerMenu.this, games.get(0));
                    String[] themes = {"starwars", "pokemon"};
                    int index = new Random().nextInt(themes.length);
                    intent.putExtra("theme", themes[index]);
                    activityResultLauncher.launch(intent);
                } else {
                    activityResultLauncher.launch(new Intent(SinglePlayerMenu.this, games.get(0)));
                }
            }
        });

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult activityResult) {
                        if(activityResult.getResultCode() == 78) {
                            Intent intent = activityResult.getData();

                            if(intent != null) {
                                score += intent.getIntExtra("score", 0);
                            }
                            nb_results++;
                            if(nb_results==nb_games) {
                                end();
                            } else {
                                playGame(nb_results);
                            }
                        }
                    }
                });
    }

    private List<Class> createGameList() {
        List<Class> games = new ArrayList<>();

        for(int i=0; i<nb_games; i++) {
            int low = 1;
            int high = 3;
            int result = new Random().nextInt(high-low) + low;

            if(result == 1) {
                games.add(SharkSlap.class);
            } else if (result == 2) {
                games.add(QuizActivity.class);
            }
        }

        return games;
    }

    private void playGame(int game) {
        if(games.get(0) == QuizActivity.class) {
            Intent intent = new Intent(SinglePlayerMenu.this, games.get(game));
            String[] themes = {"starwars", "pokemon"};
            int index = new Random().nextInt(themes.length);
            intent.putExtra("theme", themes[index]);
            activityResultLauncher.launch(intent);
        } else {
            activityResultLauncher.launch(new Intent(SinglePlayerMenu.this, games.get(game)));
        }
    }

    private void end() {
        items.setVisibility(View.GONE);
        final_score.setText(score + "pts");
        finish_screen.setVisibility(View.VISIBLE);
        if(ScoreDB.getInstance(is, os).isOnLearderboard(score)) {
            score_save.setVisibility(View.VISIBLE);
        }
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