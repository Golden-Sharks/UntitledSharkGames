package esir.progm.untitledsharkgames.jeux.feedTheShark;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import esir.progm.untitledsharkgames.MusicPlayer;
import esir.progm.untitledsharkgames.R;

public class FeedTheShark extends AppCompatActivity{
    /*                    hide UI parameters                    */
    private int hideSystemBars = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;

    private boolean isOnBackground = false;

    /*                    Attributes                    */
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private ImageView fish_imageView;
    private ImageView shark_imageView;
    private TextView score_tw;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_feed_the_shark);

        View decor = getWindow().getDecorView();
        decor.setOnSystemUiVisibilityChangeListener(visibility -> {
            if(visibility==0) {
                decor.setSystemUiVisibility(hideSystemBars);
            }
        });

        // get layout elements
        fish_imageView = findViewById(R.id.FeedSkark_fish);
        shark_imageView = findViewById(R.id.FeedSkark_shark);
        score_tw = findViewById(R.id.FeedSkark_score_text);

        // Get accelerometer
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // init score
        score = 0;

        new FeedTheSharkTask().execute();
    }

    private void setScoreText(String text) {
        score_tw.setText(text);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isOnBackground) {
            MusicPlayer.getInstance().pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isOnBackground) {
            MusicPlayer.getInstance().stop();
            MusicPlayer.getInstance().play(getApplicationContext(), R.raw.main_menu, true);
        } else {
            MusicPlayer.getInstance().resume();
            isOnBackground = false;
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if(level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN ||
                level == ComponentCallbacks2.TRIM_MEMORY_BACKGROUND ||
                level == ComponentCallbacks2.TRIM_MEMORY_MODERATE ||
                level == ComponentCallbacks2.TRIM_MEMORY_COMPLETE) {
            isOnBackground = true;
            onPause(); // https://i.kym-cdn.com/photos/images/original/000/639/420/094.gif
        }
    }

    /**********************************************************************************************/
    /*                                         Async task                                         */
    /**********************************************************************************************/
    private class FeedTheSharkTask extends AsyncTask<Void, Void, Integer> implements SensorEventListener{
        /*                 Final attributes                 */
        private final int FISH_IMAGE_WIDTH = 190;
        private final int FISH_IMAGE_HEIGHT = 50;
        private final long GAME_DURATION = 30000;
        private final long COOLDOWN = 12;
        private final int SCREEN_WIDTH = getResources().getDisplayMetrics().widthPixels;
        private final int SCREEN_HEIGHT = getResources().getDisplayMetrics().heightPixels;
        private final Random random = new Random();

        /*                    Attributes                    */
        private float x_val = 0.0f;
        private float  y_val = 0.0f;
        private float  z_val = 0.0f;
        private long last_update = 0;
        private boolean is_fish_reversed = false;
        private Animation animation;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (sensorManager != null) {
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                if (accelerometer != null) {
                    sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Sersor service unreachable", Toast.LENGTH_SHORT).show();
            }
            fish_imageView.setX(SCREEN_WIDTH/2);
            fish_imageView.setY(SCREEN_HEIGHT);
            shark_imageView.setX((SCREEN_WIDTH-shark_imageView.getWidth())/4);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            long start = System.currentTimeMillis();
            while(System.currentTimeMillis()-start < GAME_DURATION) {

                if(System.currentTimeMillis() - last_update > COOLDOWN) {
                    float target_x = fish_imageView.getX() + x_val;
                    float target_y = fish_imageView.getY() + y_val;

                    if (target_x < 0) {
                        target_x = 0;
                    } else if (target_x > SCREEN_WIDTH- FISH_IMAGE_WIDTH) {
                        target_x = SCREEN_WIDTH- FISH_IMAGE_WIDTH;
                    }

                    if (target_y < 0) {
                        target_y = 0;
                    } else if (target_y > SCREEN_HEIGHT- FISH_IMAGE_HEIGHT) {
                        target_y = SCREEN_HEIGHT- FISH_IMAGE_HEIGHT;
                    }

                    fish_imageView.setX(target_x);
                    fish_imageView.setY(target_y);

                    animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, target_x,
                            Animation.RELATIVE_TO_SELF, 0f,
                            Animation.RELATIVE_TO_SELF, target_y);

                    animation.setDuration(200);
                    animation.setFillAfter(true);
                    fish_imageView.startAnimation(animation);

                    last_update = System.currentTimeMillis();
                    double distance = computeDistance(fish_imageView.getX(), fish_imageView.getY(),
                            shark_imageView.getX(), shark_imageView.getY());

                    if(distance<80) {
                        score += 100;
                        publishProgress();
                        int fish_new_x = random.nextInt(SCREEN_WIDTH-fish_imageView.getWidth());
                        int fish_new_y = random.nextInt(SCREEN_HEIGHT-fish_imageView.getHeight());
                        int shark_new_x = random.nextInt(SCREEN_WIDTH-shark_imageView.getWidth());
                        int shark_new_y = random.nextInt(SCREEN_HEIGHT-shark_imageView.getHeight());

                        if (computeDistance(fish_new_x, fish_new_y, shark_new_x, shark_new_y) > 300) {
                            shark_imageView.setX(shark_new_x);
                            shark_imageView.setY(shark_new_y);
                            fish_imageView.setX(fish_new_x);
                            fish_imageView.setY(fish_new_y);
                        }
                    }
                }
            }
            return 0;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            if(!isFinishing())
                score_tw.setText(score + " pts");
        }

        private double computeDistance(float fish_x, float fish_y, float shark_x, float shark_y) {
            float dx = (shark_x+(shark_imageView.getWidth()/2)) - (fish_x+(fish_imageView.getWidth()/2));
            float dy = (shark_y+(shark_imageView.getHeight()/2)) - (fish_y+(fish_imageView.getHeight()/2));

            return Math.sqrt(dx*dx + dy*dy);
        }

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if(sensorEvent.sensor.getType()==Sensor.TYPE_ACCELEROMETER) {
                x_val = - sensorEvent.values[0];
                y_val = sensorEvent.values[1];
                z_val = sensorEvent.values[2];

                if(x_val<0 && !is_fish_reversed) {
                    fish_imageView.setScaleX(-1);
                    is_fish_reversed = true;
                } else if (x_val>0 && is_fish_reversed) {
                    fish_imageView.setScaleX(1);
                    is_fish_reversed = false;
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }


        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            Intent intent = new Intent();
            intent.putExtra("score", score);
            setResult(78, intent);
            //MusicPlayer.getInstance().stop();
            finish();
        }
    }
}