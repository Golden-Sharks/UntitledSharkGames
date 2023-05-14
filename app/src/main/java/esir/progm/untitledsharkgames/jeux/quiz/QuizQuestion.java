package esir.progm.untitledsharkgames.jeux.quiz;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

import esir.progm.untitledsharkgames.R;

public class QuizQuestion {
    private String question;        // La question
    private String correctAnswer;   // La bonne réponse
    private String[] wrongAnswers;  // Les mauvaises réponses
    private int idRightAnswer;      // id de la bonne réponse
    private ArrayList<String> listOfQuestions;  // Liste des différentes questions qui seront posées
    private int currentQuestion;    // id de la question en cours

    public QuizQuestion(Context context, String theme) {
        this.currentQuestion = 0;
        this.wrongAnswers = new String[3];
        Random rd = new Random();
        this.idRightAnswer = rd.nextInt(4)+1;
        InputStream is;
        // Initialisation du thème
        switch (theme) {
            case "starwars":
                is = context.getResources().openRawResource(R.raw.quiz_starwars);
                break;
            case "pokemon":
                is = context.getResources().openRawResource(R.raw.quiz_pokemon);
                break;
            case "progm":
                is = context.getResources().openRawResource(R.raw.quiz_progm);
                break;
            default:
                // Par défaut on choisit aléatoirement
                int choice = new Random().nextInt(3);
                if (choice==0) {
                    is = context.getResources().openRawResource(R.raw.quiz_starwars);
                }else if (choice==1) {
                    is = context.getResources().openRawResource(R.raw.quiz_pokemon);
                } else {
                    is = context.getResources().openRawResource(R.raw.quiz_progm);
                }
        }
        // Lis les questions voulues
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

    /**
     * Retourne la liste des réponses et la question à des indices aléatoires
     */
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

    /**
     * Indique si la réponse essayée (identifiée par l'indice) est la bonne
     */
    public boolean isRightAnswer(int indice) {
        return indice == this.idRightAnswer;
    }

    /**
     * Passe à la question suivante et retourne true
     * Sinon c'est la dernière question retourne false
     */
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

    /**
     * Permet de parser la question courante et de répartir les bonnes et mauvaises réponses
     * dans les bons objets.
     */
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

