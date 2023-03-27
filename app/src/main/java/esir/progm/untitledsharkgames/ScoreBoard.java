package esir.progm.untitledsharkgames;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ScoreBoard extends AppCompatActivity {
    private int hideSystemBars = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_score_board);

        View decor = getWindow().getDecorView();
        decor.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener(){
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility==0) {
                    decor.setSystemUiVisibility(hideSystemBars);
                }
            }
        });

        ArrayList<String> scores = loadScores();

        ListView lv = findViewById(R.id.list_scores);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                scores);

        lv.setAdapter(arrayAdapter);
        lv.setDivider(null);
        arrayAdapter.notifyDataSetChanged();

        ImageButton exit = findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ScoreBoard.this, MainMenu.class));
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(hideSystemBars);
        }
    }

    private ArrayList<String> loadScores(){
        ArrayList<String> res = new ArrayList<>();
        res.add("Requiiin...................300");
        res.add("Root.......................150");
        res.add("Spraduss...................149");
        res.add("Michel.....................101");
        res.add("Michel......................99");
        res.add("Tortue......................85");
        res.add("Empty.......................78");
        res.add("Destin......................60");
        res.add("bato........................57");
        res.add("Mar1.........................1");

        return res;
    }
}