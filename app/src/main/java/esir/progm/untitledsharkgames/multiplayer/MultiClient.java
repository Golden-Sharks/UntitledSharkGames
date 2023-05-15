package esir.progm.untitledsharkgames.multiplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.jeux.quiz.QuizActivity;

public class MultiClient extends Multijoueur {
    private TextView info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_client);
        setWidgets();
    }

    private void setWidgets() {
        ((TextView)findViewById(R.id.J1_pseudo)).setText(pseudonyme);
        info = findViewById(R.id.addressEditText);
        Button connection = findViewById(R.id.connectButton);
        connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = info.getText().toString();
                String adress = getIpFromCode(code);
                if (!adress.equals("")){
                    Communication.setIpHost(adress);
                    Communication.sendMessage("/"+pseudonyme);
                }
            }
        });

        ImageButton back = findViewById(R.id.exit);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /**
     * Parse le code renseigné par l'utilisateur pour en extraire
     * l'adresse IP de l'hébergeur de la partie
     * (les deux doivent être sur le même réseau)
     */
    private String getIpFromCode(String code) {
        try {
            String IP = server.getIpAddress();
            StringBuilder finalIP = new StringBuilder(20);
            int countDot = 0;
            int indice = 0;
            while (countDot<2){
                char c = IP.charAt(indice);
                if (c=='.') {
                    countDot++;
                }
                finalIP.append(c);
                indice++;
            }
            finalIP.append(code);
            return finalIP.toString();
        } catch(Exception e) {
            ((TextView)findViewById(R.id.infos)).setText("Pensez à activer le wifi");
            return "";
        }
    }

    /**
     * Créé la liste des jeux à partir des tirages au sort de l'hébergeur
     */
    private void createGameList() {
        this.games = new ArrayList<>();
        games.add(POUL[0][tirageAuSort[0]]);
        games.add(POUL[1][tirageAuSort[1]]);
        games.add(QuizActivity.class);
        launchGames(0);
    }

    /**
     * Interprète le message envoyé par l'hébergeur
     */
    @Override
    public void setMsg(String message) {
        if (message.length()>=2) {
            char c = message.charAt(0);
            String restOfMsg = message.substring(1);
            if (c == '[') {
                // launch infos : suite de int indiquant le résultat du tirage au sort
                this.relevant = restOfMsg.split(",");
                tirageAuSort[0] = Integer.parseInt(relevant[0]);
                tirageAuSort[1] = Integer.parseInt(relevant[1]);
                tirageAuSort[2] = Integer.parseInt(relevant[2]);
                createGameList();
            } else if (c == '_') {
                // Pseudo + score de l'hébergeur
                this.relevant = restOfMsg.split("_");
                drawAdversaryUiScores(relevant[0], Integer.parseInt(relevant[1]));
            } else if (c=='F') {
                // Signal indiquant le lancement du prochain jeu
                launchGames(nb_results);
            }
        }
    }
}