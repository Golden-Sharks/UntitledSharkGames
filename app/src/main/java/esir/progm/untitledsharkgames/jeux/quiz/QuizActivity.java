package esir.progm.untitledsharkgames.jeux.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.interfaces.Game;
import esir.progm.untitledsharkgames.ManageFiles;

public class QuizActivity extends AppCompatActivity implements Game {
    private boolean isRight;
    private QuizQuestion qq;

    private CountDownTimer mCountDownTimer;
    private String theme;

    private int score;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_quiz);

        this.intent = getIntent();
        Bundle b = this.intent.getExtras();
        if (b!=null) {
            this.theme = b.getString("theme");
        } else {
            this.theme = "";
        }

        launch();
    }

    public void launch() {
        this.isRight = false;
        this.qq = new QuizQuestion(getApplicationContext(), this.theme);
        resetQuiz();
    }

    public int getScore() {
        return this.score;
    }

    /**
     * Set the progress bar
     */
    private void setProgressBar() {
        ProgressBar pb = findViewById(R.id.progressbar);
        pb.setProgress(100);
        int nbMilliSec = 10000;
        this.mCountDownTimer = new CountDownTimer(nbMilliSec,100) {
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
     * Set the TextView containing the question
     * @param question : this question to display
     */
    private void setTextView(String question) {
        TextView score = findViewById(R.id.score);
        score.setText("Score : "+this.score);
        TextView tv = findViewById(R.id.question);
        tv.setText(question);
    }

    /**
     * Set the four buttons of the quiz interface
     * @param answers : the differents answers
     */
    private void setButtons(String[] answers) {
        Button bt1 = findViewById(R.id.rep_1);
        bt1.setText(answers[1]);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRight = qq.isRightAnswer(1);
                endRound();
            }
        });

        Button bt2 = findViewById(R.id.rep_2);
        bt2.setText(answers[2]);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRight = qq.isRightAnswer(2);
                endRound();
            }
        });

        Button bt3 = findViewById(R.id.rep_3);
        bt3.setText(answers[3]);
        bt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRight = qq.isRightAnswer(3);
                endRound();
            }
        });

        Button bt4 = findViewById(R.id.rep_4);
        bt4.setText(answers[4]);
        bt4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRight = qq.isRightAnswer(4);
                endRound();
            }
        });
    }

    private void endRound() {
        if (isRight) {
            ProgressBar pb = findViewById(R.id.progressbar);
            this.score += pb.getProgress();                                 //TODO calcul des scores
        } else {
            System.out.println("T une merde en fait?");
        }
        boolean isEnd = qq.setUpNewQuestion();
        if (!isEnd) {
            new ManageFiles(getApplicationContext()).createFile("score_tmp", this.score+"");
            System.out.println("true SCORE : "+this.score);
            this.finish();
        }
        else {
            TextView score = findViewById(R.id.score);
            score.setText("Score : "+this.score);
            resetQuiz();
        }
    }


    private void resetQuiz() {
        String[] questionStrings = qq.getInterface();
        if (this.mCountDownTimer != null) this.mCountDownTimer.cancel();
        setProgressBar();
        setTextView(questionStrings[0]);
        setButtons(questionStrings);
    }


}