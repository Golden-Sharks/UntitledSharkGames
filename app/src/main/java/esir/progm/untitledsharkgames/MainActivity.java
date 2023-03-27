package esir.progm.untitledsharkgames;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        View decor = getWindow().getDecorView();
        int options = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decor.setSystemUiVisibility(options);

        ImageView image = findViewById(R.id.image);

        Animation a = new AlphaAnimation(0.00f, 1.00f);

        a.setDuration(2000);
        a.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }

            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }

            public void onAnimationEnd(Animation animation) {
                startActivity(new Intent(MainActivity.this, MainMenu.class));
            }
        });

        image.startAnimation(a);

    }
}
