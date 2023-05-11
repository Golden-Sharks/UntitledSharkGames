package esir.progm.untitledsharkgames.jeux;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.interfaces.Game;

public class Cliquer extends Game {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliquer);
    }

    @Override
    protected void launch() {

    }

    @Override
    protected int getScore() {
        return 0;
    }
}