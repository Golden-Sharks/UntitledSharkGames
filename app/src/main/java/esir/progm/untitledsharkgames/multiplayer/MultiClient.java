package esir.progm.untitledsharkgames.multiplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import esir.progm.untitledsharkgames.Communication.Server;
import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.jeux.Cliquer;

public class MultiClient extends AppCompatActivity {

    private TextView info;

    private Server server;

    private String pseudonyme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_multi_client);
        Intent intent = getIntent();
        if (intent != null) {
            pseudonyme = intent.getStringExtra("pseudo");
        } else {
            pseudonyme = "default";
        }

        info = findViewById(R.id.addressEditText);
        Button connection = findViewById(R.id.connectButton);
        MultiClient act = this;
        connection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                server = new Server(act);
                String code = info.getText().toString();
                String adress = getIpFromCode(code);
                Communication.setIpHost(adress);
                Communication.sendMessage(pseudonyme);
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

    public void launchGames() {
        server.onDestroy();
        startActivity(new Intent(MultiClient.this, Cliquer.class));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (server!=null) server.onDestroy();
    }
}