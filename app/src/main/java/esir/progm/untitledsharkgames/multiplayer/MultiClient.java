package esir.progm.untitledsharkgames.multiplayer;

import androidx.activity.result.ActivityResultLauncher;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.jeux.quiz.QuizActivity;
import esir.progm.untitledsharkgames.jeux.sharkSlap.SharkSlap;

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
                Communication.setIpHost(adress);
                Communication.sendMessage("/"+pseudonyme);
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

    private String getIpFromCode(String code) {
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
        System.out.println("IP : "+finalIP);
        return finalIP.toString();
    }

    private void createGameList() {
        this.games = new ArrayList<>();
        games.add(POUL[0][tirageAuSort[0]]);
        games.add(POUL[1][tirageAuSort[1]]);
        games.add(QuizActivity.class);
        launchGames(0);
    }

    @Override
    public void setMsg(String message) {
        if (message.length()>=2) {
            char c = message.charAt(0);
            String restOfMsg = message.substring(1);
            if (c == '[') {
                // launch infos : suite de int
                this.relevant = restOfMsg.split(",");
                tirageAuSort[0] = Integer.parseInt(relevant[0]);
                tirageAuSort[1] = Integer.parseInt(relevant[1]);
                tirageAuSort[2] = Integer.parseInt(relevant[2]);
                createGameList();
            } else if (c == '_') {
                // Pseudo + score
                this.relevant = restOfMsg.split("_");
                System.out.println("RECEIVED SCORE : "+relevant[0]+"/"+relevant[1]);
                playGame(nb_results);
            }
        }
    }
}