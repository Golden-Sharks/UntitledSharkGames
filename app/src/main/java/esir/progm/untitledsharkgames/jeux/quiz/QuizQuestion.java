package esir.progm.untitledsharkgames.jeux.quiz;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

import esir.progm.untitledsharkgames.R;

public class QuizQuestion {

    private String question;
    private String correctAnswer;
    private String[] wrongAnswers;
    private int idRightAnswer;

    private ArrayList<String> listOfQuestions;

    private int currentQuestion;

    public QuizQuestion(Context context, String theme) {
        this.currentQuestion = 0;
        this.wrongAnswers = new String[3];
        Random rd = new Random();
        this.idRightAnswer = rd.nextInt(4)+1;
        InputStream is;
        switch (theme) {
            case "starwars":
                is = context.getResources().openRawResource(R.raw.quiz_starwars);
                break;
            case "pokemon":
                is = context.getResources().openRawResource(R.raw.quiz_pokemon);
                break;
            default:
                is = context.getResources().openRawResource(R.raw.quiz_starwars);
        }
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        String line;
        listOfQuestions = new ArrayList<>();
        try {
            while ((line=r.readLine()) != null) {
                listOfQuestions.add(line);
            }
            parseQuestion();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String[] getInterface(){
        String[] ret = new String[5];
        ret[0] = this.question;
        int indice = 0;
        for (int i=1 ; i<5 ; i++) {
            if (i==this.idRightAnswer) {
                ret[i] = this.correctAnswer;
            } else {
                ret[i] = this.wrongAnswers[indice];
                indice++;
            }
        }
        return ret;
    }

    public boolean isRightAnswer(int indice) {
        return indice == this.idRightAnswer;
    }

    public boolean setUpNewQuestion() {
        this.currentQuestion++;
        if (this.currentQuestion<listOfQuestions.size()) {
            parseQuestion();
            Random rd = new Random();
            this.idRightAnswer = rd.nextInt(4)+1;
            return true;
        } else {
            return false;
        }
    }

    private void parseQuestion() {
        String current = this.listOfQuestions.get(this.currentQuestion);
        String[] splitted = current.split(",");
        this.question = splitted[0];
        this.correctAnswer = splitted[1];
        this.wrongAnswers[0] = splitted[2];
        this.wrongAnswers[1] = splitted[3];
        this.wrongAnswers[2] = splitted[4];
    }
}
