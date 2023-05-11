package esir.progm.untitledsharkgames.multiplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.List;

import esir.progm.untitledsharkgames.Communication.Server;
import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.jeux.Cliquer;

public class MultiHeberger extends AppCompatActivity {

    private ArrayAdapter listDevices;
    private HashMap<String, String> nameToAdress;
    private List<String> listOfNames;
    private String pseudonyme;
    private Server server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_multi_heberger);
        Intent intent = getIntent();
        if (intent != null) {
            pseudonyme = intent.getStringExtra("pseudo");
        } else {
            pseudonyme = "default";
        }
        server = new Server(this);
        setWidgets();
        setList();
    }

    private void setWidgets() {
        TextView infoip = findViewById(R.id.infoip);
        infoip.setText("Votre code : "+getCode());
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
                Communication.sendMessage(pseudonyme);
            }
        });
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listOfNames);
        lv.setAdapter(arrayAdapter);
        this.listDevices = arrayAdapter;
    }

    public void addDevice(String name, String adress) {
        listOfNames.add(name);
        nameToAdress.put(name, adress);
        listDevices.notifyDataSetChanged();
    }

    public void launchGames() {
        server.onDestroy();
        startActivity(new Intent(MultiHeberger.this, Cliquer.class));
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
    protected void onDestroy() {
        super.onDestroy();
        server.onDestroy();
    }
}