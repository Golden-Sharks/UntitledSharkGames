package esir.progm.untitledsharkgames.interfaces;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public abstract class Game extends AppCompatActivity {

    private TextView msg;

    public void setTextView(TextView msg) {
        this.msg = msg;
    }

    public void setMsg(String message) {
        msg.setText(message);
    }

    protected abstract void launch();
    protected abstract int getScore();
}
