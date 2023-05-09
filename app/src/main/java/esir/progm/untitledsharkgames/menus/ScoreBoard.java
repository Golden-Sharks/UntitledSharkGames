package esir.progm.untitledsharkgames.menus;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.android.material.shape.ShapePath;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import esir.progm.untitledsharkgames.R;
import esir.progm.untitledsharkgames.ScoreDB;

public class ScoreBoard extends AppCompatActivity {
    private int hideSystemBars = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

    private InputStream is;
    private FileOutputStream os;

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

        HashMap<String, ArrayList<String>> map = loadScores(this.getApplicationContext());
        ArrayList<String> users = map.get("users");
        ArrayList<String> scores = map.get("scores");

        ListView lv_users = findViewById(R.id.list_names);
        ListView lv_scores = findViewById(R.id.list_scores);
        ArrayAdapter<String> arrayAdapter_users = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                users);

        ArrayAdapter<String> arrayAdapter_scores = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                scores);

        lv_users.setAdapter(arrayAdapter_users);
        lv_users.setDivider(null);
        lv_scores.setAdapter(arrayAdapter_scores);
        lv_scores.setDivider(null);
        arrayAdapter_scores.notifyDataSetChanged();

        ImageButton exit = findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

    private HashMap<String, ArrayList<String>> loadScores(Context context){
        ArrayList<String> users = new ArrayList<>();
        ArrayList<String> scores = new ArrayList<>();

        is = getApplicationContext().getResources().openRawResource(R.raw.scores);
        try {
            os = getApplicationContext().openFileOutput("scores.txt", Context.MODE_PRIVATE);
        } catch (Exception e) {
            System.out.println("Ficher scores plus là !");
        }

        List<Pair<String, Integer>> leaderborad = ScoreDB.getInstance(is, os).getDB();

        for(Pair<String, Integer> leader: leaderborad) {
            users.add(leader.first);
            scores.add(leader.second+"");
        }
        HashMap<String, ArrayList<String>> retMap = new HashMap<>();
        retMap.put("users", users);
        retMap.put("scores", scores);
        return retMap;
    }
}