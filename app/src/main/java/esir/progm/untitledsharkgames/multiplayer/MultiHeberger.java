package esir.progm.untitledsharkgames.multiplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.jeux.quiz.QuizActivity;

public class MultiHeberger extends Multijoueur {

    private ArrayAdapter listDevices;
    private HashMap<String, String> nameToAdress;
    private List<String> listOfNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_heberger);
        ((TextView)findViewById(R.id.J1_pseudo)).setText(pseudonyme);
        setWidgets();
        setList();
    }

    private void setWidgets() {
        TextView infoip = findViewById(R.id.infoip);
        if (infoip != null) infoip.setText("Votre code : "+getCode());
        ImageButton back = findViewById(R.id.exit);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Button clear = findViewById(R.id.clearButton);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameToAdress.clear();
                listOfNames.clear();
                listDevices.notifyDataSetChanged();
            }
        });
    }

    private void setList() {
        nameToAdress = new HashMap<String, String>();
        listOfNames = new LinkedList<>();
        ListView lv = findViewById(R.id.listDevices);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String name = (String) lv.getItemAtPosition(position);
                String adress = nameToAdress.get(name);
                Communication.setIpHost(adress);
                createGameList();
                launchGames(0);
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, listOfNames);
        lv.setAdapter(arrayAdapter);
        this.listDevices = arrayAdapter;
    }

    public void addDevice(String name, String adress) {
        listOfNames.add(name);
        nameToAdress.put(name, adress);
        listDevices.notifyDataSetChanged();
    }

    private void createGameList() {
        games = new LinkedList<>();
        Random rd = new Random();
        this.tirageAuSort[0] = rd.nextInt(POUL[0].length);
        this.tirageAuSort[1] = rd.nextInt(POUL[1].length);
        this.tirageAuSort[2] = rd.nextInt(3);
        games.add(POUL[0][tirageAuSort[0]]);
        games.add(POUL[1][tirageAuSort[1]]);
        games.add(QuizActivity.class);
        String message = "["+tirageAuSort[0]+","+tirageAuSort[1]+","+tirageAuSort[2];
        Communication.sendMessage(message);
    }

    private String getCode() {
        String IP = server.getIpAddress();
        StringBuilder code = new StringBuilder(20);
        int countDot = 0;
        for (int i=0 ; i<IP.length() ; i++){
            char c = IP.charAt(i);
            if (c=='.') {
                countDot++;
            }
            if (countDot>2 || (countDot==2 && c!='.')) {
                code.append(c);
            }
        }
        return code.toString();
    }

    @Override
    public void setMsg(String message) {
        char c = message.charAt(0);
        String restOfMsg = message.substring(1);
        if (c == '/') {
            // IP + pseudo
            this.relevant = restOfMsg.split("/");
            String pseudo = relevant[0];
            String ip = relevant[1];
            addDevice(pseudo, ip);
        } else if (c == '_') {
            // Pseudo + score
            this.relevant = restOfMsg.split("_");
            drawAdversaryUiScores(relevant[0], Integer.parseInt(relevant[1]));
        }
    }

    protected void onGameResult(ActivityResult activityResult) {
        super.onGameResult(activityResult);
        if (nb_results!=3) {
            Button next = findViewById(R.id.next);
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Communication.sendMessage("F_");
                    launchGames(nb_results);
                }
            });
        }
    }
}