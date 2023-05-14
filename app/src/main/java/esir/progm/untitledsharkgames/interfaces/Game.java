package esir.progm.untitledsharkgames.interfaces;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public abstract class Game extends AppCompatActivity {

    protected abstract void launch();
    protected abstract int getScore();
}
