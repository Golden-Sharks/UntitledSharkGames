package esir.progm.untitledsharkgames.jeux;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import esir.progm.untitledsharkgames.Communication.Server;
import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.interfaces.Game;
import esir.progm.untitledsharkgames.multiplayer.Communication;

public class Cliquer extends Game {

    private Server server;
    private int count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliquer);
        Button clique = findViewById(R.id.clique);
        super.setTextView(findViewById(R.id.msg));
        this.count = 0;
        server = new Server(this);
        clique.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                Communication.sendMessage("TEST : "+count);
            }
        });
    }

    @Override
    protected void launch() {

    }

    @Override
    protected int getScore() {
        return 0;
    }
}