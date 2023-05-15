package esir.progm.untitledsharkgames.jeux.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import esir.progm.untitledsharkgames.MusicPlayer;
import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.ManageFiles;

public class QuizActivity extends AppCompatActivity {
    private final int DURATION = 6000;      // Durée d'une question en milli-secondes
    /*              HIDE UI PARAMETERS              */
    private int hideSystemBars = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    private boolean isOnBackground = false;
    private boolean isRight; // Le joueur à cliquer sur la bonne réponse
    private QuizQuestion qq; // L'instance de QuizQuestion permet de charger les question et les réponses
    private CountDownTimer mCountDownTimer; // Timer de chaque question
    private String theme; // Theme du quiz
    private int score; // Score du joueur

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_quiz);
        View decor = getWindow().getDecorView();
        decor.setOnSystemUiVisibilityChangeListener(visibility -> {
            if(visibility==0) {
                decor.setSystemUiVisibility(hideSystemBars);
            }
        });
        // Set up le theme en fonction de l'intent utilisé pour créer l'activité
        Bundle b = getIntent().getExtras();
        if (b!=null) {
            this.theme = b.getString("theme");
        } else {
            this.theme = "";
        }
        launch();
    }

    /**
     * Initialisation
      */
    private void launch() {
        this.isRight = false;
        this.qq = new QuizQuestion(getApplicationContext(), this.theme);
        resetQuiz();
    }

    /**
     * Set la progress bar
     */
    private void setProgressBar() {
        ProgressBar pb = findViewById(R.id.progressbar);
        pb.setProgress(100);
        this.mCountDownTimer = new CountDownTimer(DURATION,DURATION/100) {
            @Override
            public void onTick(long millisUntilFinished) {
                int current = pb.getProgress();
                pb.setProgress(current - 1);
            }
            @Override
            public void onFinish() {
                endRound();
            }
        };
        mCountDownTimer.start();
    }

    /**
     * Set up la textView qui contient la question
     * @param question : La question à afficher
     */
    private void setTextView(String question) {
        TextView score = findViewById(R.id.score);
        score.setText("Score : "+this.score);
        TextView tv = findViewById(R.id.question);
        tv.setText(question);
    }

    /**
     * Set up les 4 boutons de l'interface
     * @param answers : les différentes réponses
     */
    private void setButtons(String[] answers) {
        Button bt1 = findViewById(R.id.rep_1);
        bt1.setText(answers[1]);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRight = qq.isRightAnswer(1);
                changeRectangleColor(1);
            }
        });

        Button bt2 = findViewById(R.id.rep_2);
        bt2.setText(answers[2]);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRight = qq.isRightAnswer(2);
                changeRectangleColor(2);
            }
        });

        Button bt3 = findViewById(R.id.rep_3);
        bt3.setText(answers[3]);
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRight = qq.isRightAnswer(3);
                changeRectangleColor(3);
            }
        });

        Button bt4 = findViewById(R.id.rep_4);
        bt4.setText(answers[4]);
        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRight = qq.isRightAnswer(4);
                changeRectangleColor(4);
            }
        });
        changeRectangleColor(0);
    }

    /**
     * Change la couleur du rectangle désigné
     */
    private void changeRectangleColor(int indice) {
        Button[] reponses = {findViewById(R.id.rep_1),findViewById(R.id.rep_2),findViewById(R.id.rep_3),findViewById(R.id.rep_4)};
        for (int i=1 ; i<5 ; i++) {
            Button current = reponses[i-1];
            if (i==indice) {
                current.setBackgroundColor(getResources().getColor(R.color.persoLightBlue));
            } else {
                current.setBackgroundColor(getResources().getColor(R.color.persoWhite));
            }
        }

    }

    /**
     * Met fin au round en cours
     */
    private void endRound() {
        if (isRight) {
            this.score += 100;
            isRight = false;
        }
        boolean isEnd = qq.setUpNewQuestion();
        if (!isEnd) {
            new ManageFiles(getApplicationContext()).createFile("score_tmp", this.score+"");
            System.out.println("true SCORE : "+this.score);
            Intent intent = new Intent();
            intent.putExtra("score", score);
            setResult(78, intent);
            this.finish();
        }
        else {
            TextView score = findViewById(R.id.score);
            score.setText("Score : "+this.score);
            resetQuiz();
        }
    }

    /**
     * Change de question
     */
    private void resetQuiz() {
        String[] questionStrings = qq.getInterface();
        if (this.mCountDownTimer != null) this.mCountDownTimer.cancel();
        setProgressBar();
        setTextView(questionStrings[0]);
        setButtons(questionStrings);
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
