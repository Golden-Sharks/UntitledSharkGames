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
import android.widget.ImageButton;
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
import esir.progm.untitledsharkgames.jeux.WhrilOtter.WhrilOtter;
import esir.progm.untitledsharkgames.jeux.feedTheShark.FeedTheShark;
import esir.progm.untitledsharkgames.jeux.quiz.QuizActivity;
import esir.progm.untitledsharkgames.jeux.sharkSlap.SharkSlap;
import esir.progm.untitledsharkgames.jeux.spinTheShark.SpinTheShark;

public class SinglePlayerMenu extends AppCompatActivity {
    /*                    hide UI parameters                    */
    private int hideSystemBars = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    private boolean isOnBackground = false;

    /*                    activity parameters                   */
    private final int MAX_GAMES_NUMBER = 99;
    private int score;
    private int nb_games;
    private List<Class> games;
    private RelativeLayout items;
    private RelativeLayout finish_screen;
    private LinearLayout score_save;
    private Button score_submit;
    private EditText username_feild;
    private TextView final_score;
    public ActivityResultLauncher<Intent> activityResultLauncher;
    private int nb_results;
    FileOutputStream os;
    InputStream is;
    private final Class[][] POUL = {{WhrilOtter.class, FeedTheShark.class},{SpinTheShark.class, SharkSlap.class}}; // Différents jeux qu'il est possible de lancer

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Init activity and disable UI
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

        // Init attributes
        nb_games = 1;
        score = 0;
        games = createGameList();
        nb_results = 0;

        is = getApplicationContext().getResources().openRawResource(R.raw.scores);
        try {
            os = getApplicationContext().openFileOutput("scores.txt", Context.MODE_PRIVATE);
        } catch (Exception e) {
            System.out.println("Ficher scores plus là !");
        }

        // Get layout elements
        TextView up = findViewById(R.id.up_games);
        TextView down = findViewById(R.id.down_games);
        TextView nb_game_text = findViewById(R.id.nb_games);
        items = findViewById(R.id.items);
        finish_screen = findViewById(R.id.score_screen);
        score_save = findViewById(R.id.score_save);
        score_submit = findViewById(R.id.save_score);
        username_feild = findViewById(R.id.username_text);
        final_score = findViewById(R.id.score);;

        // Set event listeners
        score_submit.setOnClickListener(view -> {
            String username = username_feild.getText().toString();
            if (isPseudoValid(username)) {
                ScoreDB.getInstance(is, os).addOnLeaderboard(username, score);
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
        start.setOnClickListener(view -> {
            games = createGameList();
            if(games.get(0) == QuizActivity.class) {
                Intent intent = new Intent(SinglePlayerMenu.this, games.get(0));
                String[] themes = {"starwars", "pokemon", "progm"};
                int index = new Random().nextInt(themes.length);
                intent.putExtra("theme", themes[index]);
                activityResultLauncher.launch(intent);
            } else {
                activityResultLauncher.launch(new Intent(SinglePlayerMenu.this, games.get(0)));
            }
        });
        ImageButton exit = findViewById(R.id.singleplayer_exit);
        exit.setOnClickListener(view -> finish());

        // Set activity launcher
        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                activityResult -> {
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
                });
    }

    /**
     * Check if selected pseudonym is valid
     * @param username -> string
     * @return boolean : True if username is a valid pseudonym, False otherwise
     */
    private boolean isPseudoValid(String username) {
        TextView error = findViewById(R.id.errorForLeaderboard);
        if (username.length()==0) {
            error.setText("Renseignez un pseudonyme");
            return false;
        }
        for (int i=0 ; i<username.length() ; i++){
            if (i==25) {
                error.setText("Le pseudo est trop long\n(il doit faire moins de 25 caractères)");
                return false;
            }
            char c = username.charAt(i);
            if (c==' ') {
                error.setText("Le pseudo ne doit pas contenir d'espaces");
                return false;
            }
        }
        return true;
    }

    /**
     * Create a game list
     * @return list of Class, size=nb_games
     */
    private List<Class> createGameList() {
        // Create empty list
        List<Class> games = new ArrayList<>();

        // Loop for games number
        for(int i=0; i<nb_games; i++) {
            int j = i%3;
            // Generate a number between 1 and number of available games
            if (j==2) {
                games.add(QuizActivity.class);
            } else {
                int random = new Random().nextInt(POUL[j].length);
                games.add(POUL[j][random]);
            }
        }
        return games;
    }

    /**
     * Launch a single player game
     * @param game -> Class refers to the game to launch
     */
    private void playGame(int game) {
        // If first game is a quizz, randomly choose the theme, otherwise launch game directly
        if(games.get(0) == QuizActivity.class) {
            Intent intent = new Intent(SinglePlayerMenu.this, games.get(game));
            String[] themes = {"starwars", "pokemon", "progm"};
            int index = new Random().nextInt(themes.length);
            intent.putExtra("theme", themes[index]);
            activityResultLauncher.launch(intent);
        } else {
            activityResultLauncher.launch(new Intent(SinglePlayerMenu.this, games.get(game)));
        }
    }

    /**
     * Change UI at the end of a game loop
     */
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