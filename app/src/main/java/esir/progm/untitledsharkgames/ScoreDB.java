package esir.progm.untitledsharkgames;

import android.util.Pair;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ScoreDB {
    // instance
    private static ScoreDB instance;

    /*                    attributes                    */
    private ArrayList<Pair<String, Integer>> leaderborad;
    private InputStream is;
    private FileOutputStream os;

    private ScoreDB(InputStream inputStream, FileOutputStream fileOutputStream) {
        is = inputStream;
        os = fileOutputStream;
        leaderborad = loadScores();
    }
    public static ScoreDB getInstance(InputStream inputStream, FileOutputStream fileOutputStream) {
        if (instance == null) {
            instance = new ScoreDB(inputStream, fileOutputStream);
        }
        return instance;
    }

    /**
     * Returns bests scores
     * @return list of Pair username, score
     */
    private ArrayList<Pair<String, Integer>> loadScores(){
        ArrayList<Pair<String, Integer>> scores = new ArrayList<>();

        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        String line;
        try {
            while ((line=r.readLine()) != null) {
                String[] splited = line.split(",");
                Pair<String, Integer> score = new Pair<>(splited[0], Integer.parseInt(splited[1]));
                scores.add(score);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scores;
    }

    /**
     * Check is a score can be in the leaderboard
     * @param score score to analyse
     * @return boolean : True if score is on the leaderboard, False otherwise
     */
    public boolean isOnLearderboard(int score) {
        for (Pair<String, Integer> leader: leaderborad ) {
            if(score > leader.second) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add new score in the leaderboard
     * @param username new score username
     * @param score associate score
     */
    public void addOnLeaderboard(String username, int score) {
        if(isOnLearderboard(score)) {
            for(int i=0; i<leaderborad.size(); i++) {
                if(score > leaderborad.get(i).second) {
                    leaderborad.add(i, new Pair<>(username, score));
                    leaderborad.remove(leaderborad.size()-1);
                    break;
                }
            }
        }
        saveScoreBoard();
    }

    /**
     * Save leaderboard in internal storage
     */
    public void saveScoreBoard() {
        try{
            os.getChannel().truncate(0);

            for (Pair score: leaderborad ) {
                String data = score.first+","+score.second;
                os.write(( data + System.getProperty("line.separator")).getBytes());
            }
            os.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // Debug function
    public void printDB() {
        for (Pair<String, Integer> score : leaderborad) {
            System.out.print("<" + score.first + ", " + score.second + "> / ");
        }
    }

    public List<Pair<String, Integer>> getDB(){return leaderborad;}
}
