package esir.progm.untitledsharkgames;

import android.content.Context;
import android.widget.Toast;

import esir.progm.untitledsharkgames.interfaces.WifiP2P;

public class SetUpGame {
    /*
    Permet de préparer un jeu complet (enchainement de plusieurs "sous-jeux").
    Lance 3 jeux choisits aléatoirement (1 par catégories):
        - quiz (thème choisit aléatoirement)
        - capteurs
        - mouvement écran
     */

    private boolean multiplayer;
    private Context context;
    private WifiP2P link;

    public SetUpGame(WifiP2P link, Context context) {
        if (link!=null) this.multiplayer = true;
        else this.multiplayer = false;
        this.link = link;
    }

    /**
     * Envoie le score à l'adversaire ou l'affiche dans
     * un toast si le mode multijoueur n'est pas activé
     */
    private void sendScore() {
        ManageFiles mf = new ManageFiles(context);
        String score = "";
        if (mf.exists("score_tmp")) {
            score = mf.readFile("score_tmp");
            System.out.println("SCORE : "+score);
            mf.erase("score_tmp");
        }
        if (multiplayer) {
            link.sendString(score);
        } else {
            Toast.makeText(context, ("Votre score : "+score), Toast.LENGTH_LONG).show();
        }
    }
}
