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
import esir.progm.untitledsharkgames.jeux.sharkSlap.SharkSlap;

public class MultiClient extends Multijoueur {
    private TextView info;
    public ActivityResultLauncher<Intent> activityResultLauncher;

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

    private void createGameList(int jeu1, int jeu2, int jeu3) {
        this.games = new ArrayList<>();
        games.add(SharkSlap.class);
        launchGames();
    }

    @Override
    public void setMsg(String message) {
        if (message.length()>=2) {
            char c = message.charAt(0);
            String restOfMsg = message.substring(1);
            if (c == '[') {
                // launch infos : suite de int
                this.relevant = restOfMsg.split(",");
                createGameList(Integer.parseInt(relevant[0]),
                        Integer.parseInt(relevant[1]), Integer.parseInt(relevant[2]));
            } else if (c == '_') {
                // Pseudo + score
                this.relevant = restOfMsg.split("_");
                System.out.println("RECEIVED SCORE : "+relevant[0]+"/"+relevant[1]);
                playGame(nb_results);
            }
        }
    }
}