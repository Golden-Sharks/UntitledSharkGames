package esir.progm.untitledsharkgames.jeux.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import esir.progm.untitledsharkgames.R;

public class QuizActivity extends AppCompatActivity {
    private boolean isRight;
    private QuizQuestion qq;

    private CountDownTimer mCountDownTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_quiz);

        String theme;
        Bundle b = getIntent().getExtras();
        if (b!=null) {
            theme = b.getString("theme");
        } else {
            theme = "";
        }
        this.isRight = false;
        this.qq = new QuizQuestion(getApplicationContext(), theme);
        resetQuiz();
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
            System.out.println("GG");
            ProgressBar pb = findViewById(R.id.progressbar);
            int points = pb.getProgress();
        } else {
            System.out.println("T une merde en fait?");
        }
        boolean isEnd = qq.setUpNewQuestion();
        if (!isEnd) {
            this.finish();
        }
        else {
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