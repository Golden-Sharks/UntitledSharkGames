package esir.progm.untitledsharkgames.multiplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;

import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.jeux.sharkSlap.SharkSlap;

public class MultiHeberger extends Multijoueur {

    private ArrayAdapter listDevices;
    private HashMap<String, String> nameToAdress;
    private List<String> listOfNames;
    private int[] tirageAuSort;

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
                launchGames();
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




    private void /*List<Class>*/ createGameList() {
        /*List<Class> games = new ArrayList<>();
        // Choix aléatoire : 1 jeu tiré au sort par catégorie
        for(int i=0; i<3; i++) {
            int low = 1;
            int high = 3;
            int result = new Random().nextInt(high-low) + low;

            if(result == 1) {
                games.add(SharkSlap.class);
            } else if (result == 2) {
                games.add(QuizActivity.class);
            }
        }
        return games;*/
        games = new LinkedList<>();
        this.tirageAuSort = new int[3];
        this.tirageAuSort[0] = 1;
        this.tirageAuSort[1] = 2;
        this.tirageAuSort[2] = 3;
        games.add(SharkSlap.class);
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
            System.out.println("RECEIVED SCORE : "+relevant[0]+"/"+relevant[1]);
            playGame(nb_results);
        }
    }
}