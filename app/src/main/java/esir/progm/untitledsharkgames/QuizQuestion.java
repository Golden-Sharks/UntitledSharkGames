package esir.progm.untitledsharkgames;

import java.util.Random;

public class QuizQuestion {

    private String question;
    private String correctAnswer;
    private String[] wrongAnswers;
    private int idRightAnswer;

    public QuizQuestion() {
        this.wrongAnswers = new String[3];
        /**
         * TODO read the CSV to extract a question in the given theme
         */
        this.question = "Quel seigneur Sith n'apparait pas\ndans les films Star Wars";
        this.correctAnswer = "Darth Nihilus";
        this.wrongAnswers[0] = "Darth Tyrannus";
        this.wrongAnswers[1] = "Darth Sidious";
        this.wrongAnswers[2] = "Darth Maul";
        /**
         *
         */
        Random rd = new Random();
        this.idRightAnswer = rd.nextInt(4)+1;
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
}
